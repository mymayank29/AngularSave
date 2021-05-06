package com.cvx.cdf.dwepcr.producer.npttoinvoice.preprocessing

import com.cvx.cdf.dwepcr.producer.npttoinvoice.models.{CostDetail, JobIntervalProblem}
import com.cvx.cdf.dwepcr.producer.npttoinvoice.models.enumaration.{InternalProblemType, Status, TypeDetail}
import com.cvx.cdf.dwepcr.producer.normalizeCodeUDF
import com.cvx.cdf.dwepcr.producer.service._
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.col

object JobIntervalProblemPreprocessor extends Serializable{
//  import DataManagerDB._
  import SparkConnector.spark
  import spark.implicits._
  import CommonUDF._

  def isEqual(str1: String, str2: String): Boolean ={
    str1 != null &&  str1.equals(str2)
  }       

  def calculateInternalProblemType(intervalProblem: JobIntervalProblem):String = {
    val isChevron = intervalProblem.parent_name != null && intervalProblem.parent_name.contains(Config.CHEVRON_VENDOR_NAME)
    if(isEqual(intervalProblem.status, Status.NPT) && (isEqual(intervalProblem.typdetail,TypeDetail.WTHR_OCEAN) || isEqual(intervalProblem.typdetail, TypeDetail.WTHR_X_COND) || isEqual(intervalProblem.typdetail, TypeDetail.WTHR))) {
      InternalProblemType.DISCOUNT_ALL
    } else if(!isChevron && isEqual(intervalProblem.status, Status.NPT)) {
      InternalProblemType.DISCOUNT_RESPONSIBLE_VENDOR
    } else {
      InternalProblemType.NOT_DISCOUNTED
    }
  }

  val isApplied = (problemType:String, jobIntervalProblemVendorName: String, costDetailVendorName: String) => {
    if(problemType.equals(InternalProblemType.DISCOUNT_ALL)){
      true
     } else if(problemType.equals(InternalProblemType.DISCOUNT_RESPONSIBLE_VENDOR)
      && jobIntervalProblemVendorName == costDetailVendorName){
      true
    } else {
      false
    }
  }

  def getInternalProblemWithDependency: Dataset[(JobIntervalProblem, CostDetail)] = {

// 20 December new logic implementation

    val wvCostDetail = DataManager.get.wvJobIntervalProblem.as("npt").
      withColumn("nptstart_date", unixTimeToDateTime($"npt.dttmstart")).
      withColumn("nptend_date", unixTimeToDateTime($"npt.dttmend")).
      join(DataManager.get.wvWellHeader.as("wh"), $"wh.idwell" === $"npt.idwell", "inner").
      join(DataManager.get.wvJob.as("j"), $"npt.idrecparent" === $"j.idrec", "inner").
      join(DataManager.get.wvJobServiceContract.as("sc"), $"sc.idrec" === $"npt.idrecjobservicecontract", "left").
      join(DataManager.get.wvJobReport.as("do"), $"do.idrecparent" === $"npt.idrecparent" && $"npt.idrecjobprogramphasecalc" === $"do.idrecjobprogramphasecalc","left").
      withColumn("jobreport_date", unixTimeToStringDate($"do.dttmstart")).
      // remove idprogram phasecalc and email results
      // Whenever Chevron is not the Vendor WTHR event then its DQ issue --> So remove those recs
      join(DataManager.get.wvJobReportCostGen.as("cj"), $"cj.idrecparent" === $"do.idrec" && $"cj.idwell" === $"wh.idwell", "left"). 
      //removed  && $"cj.vendor" === $"sc.servicecompany" 
      //  remove the costgen table --> map afe to job table 
      withColumn("cost_gen_id", $"cj.idrec").
      join(DataManager.get.wvVendorParentMapping.as("vm"), lower($"vm.wv_vendor_name") === lower($"cj.vendor"), "left").
//      join(DataManager.get.wvVendorParentMapping.as("vm2"), lower($"vm2.wv_vendor_name") === lower($"sc.servicecompany"), "left").
//      withColumn("servicecompany_parent",$"vm2.parent_name").
      join(DataManager.get.wvJobAfe.as("afe"), $"afe.idrec" === $"cj.idrecafecustom" && $"afe.idrecparent" === $"do.idrecparent", "left").
      withColumn("code", concat(normalizeCodeUDF('Code1), normalizeCodeUDF('Code2))).
      withColumn("costdetail_cost", 'cost.cast(DoubleType)).
      withColumn("afenumber", trim(regexp_replace('afenumber,"-",""))).
//      groupBy($"wh.wellname", $"j.wvTyp", $"cj.idrecafecustom", $"npt.typ", $"sc.servicecompanyparent", $"npt.idwell".as("idwell"), $"npt.idrec".as("idrec"), $"npt.idrecparent".as("job_id"), $"npt.refno".as("refno"), $"j.jobTyp".as("jobtyp"), trim(regexp_replace('afenumber,"-","")).as("afenumber"), $"sc.Servicetyp".as("servicetyp"), 'jobreport_date, 'nptstart_date, 'nptend_date, $"vm.parent_name").
//      agg(sum('cost).as("cost"), first('cost_gen_id).as("cost_gen_id")).
      groupBy('afenumber, $"npt.idrecparent".as("job_id"), $"npt.idrec".as("idrec"), $"vm.parent_name", 'nptstart_date).
      agg(collect_set('code).as("code")).
      filter("afenumber is not null").as[CostDetail].distinct().cache()

      println(wvCostDetail.where('parent_name === Config.CHEVRON_VENDOR_NAME).count() + "<== wvCostDetail")

      println(wvCostDetail.where('parent_name === Config.CHEVRON_VENDOR_NAME).distinct())

//      wvCostDetail.write.parquet("/user/svccdfpd/tmp/wvCostDetail_192020_1329.parquet")


    @transient val isBetween = {
      to_date('jobreport_date_timelog) >= unixTimeToStringDate('dttmstart) && to_date('jobreport_date_timelog) <= unixTimeToStringDate('dttmend)
    }
    val jobRepTimeLogToJobReport = JobRepTimeLogToJobReport.getJobRepTimeLogToJobReport.
      join(DataManager.get.wvJobIntervalProblem.as("wvJobIntervalProblem"), $"refno" === $"npt_num" && isBetween && $"wvJobIntervalProblem.idrecparent" === $"job_id_timelog").
      groupBy($"wvJobIntervalProblem.idrec".as("idrecJobIntervalProblem")).
      agg(flatterStrSeqUDF(collect_set($"gl_code")).as("gl_code"),
        concat_ws(". ", collect_set('costgen_des)).as("costgen_des"),
        concat_ws(". ", collect_set('activitydescr)).as("activitydescr"))

    val nptDetailCodeMapping_ = DataManager.get.nptDetailCodeMapping.
      withColumnRenamed("Major Category", "npt_type").
      withColumnRenamed("Detail", "npt_type_detail").
      withColumnRenamed("Description", "npt_type_detail_description").
      withColumn("npt_type_typedetail_concat", concat_ws("_", 'npt_type, 'npt_type_detail))
      
    val afeToJobReportDS = DataManager.get.wvJobReport.as("do").
      join(DataManager.get.wvJobReportCostGen.as("cj"), $"cj.idrecparent" === $"do.idrec", "left").
      join(DataManager.get.wvJobAfe.as("afe"), $"afe.idrec" === $"cj.idrecafecustom" && $"afe.idrecparent" === $"do.idrecparent", "left").
//      join(DataManager.get.wvVendorParentMapping.as("vm"), lower($"vm.wv_vendor_name") === lower($"cj.vendor"), "left").
      withColumn("cost_gen_id", $"cj.idrec").
      select($"do.idrecparent".as("idrecjob"), $"cj.idwell",
      $"do.idrecjobprogramphasecalc", 
      unixTimeToStringDate('dttmstart).as("jobreport_date"),
      trim(regexp_replace('afenumber,"-","")).as("afenumber")
//      $"vm.parent_name".as("afeParentName")
      ).distinct()
      
    val intervalProblemDS = DataManager.get.wvJobIntervalProblem.as("npt").
      join(DataManager.get.wvWellHeader.as("wh"), $"wh.idwell" === $"npt.idwell", "inner").
      join(DataManager.get.wvJob.as("j"), $"npt.idrecparent" === $"j.idrec", "inner").
      drop($"j.idwell").
      join(DataManager.get.wvJobServiceContract.as("sc"), $"sc.idrec" === $"npt.idrecjobservicecontract", "left").
      drop($"sc.idrec").drop($"sc.idrecparent").drop($"sc.idwell").
      join(DataManager.get.wvJobRig.as("jr"), $"npt.idreclastrigcalc" === $"jr.idrec", "left"). // full join
      //join(DataManager.get.wvJobRig.as("jr"), $"npt.idreclastrigcalc" === $"jr.idrec", "fullouter").
      drop($"jr.idwell").drop($"jr.idrec").
      join(DataManager.get.wvVendorParentMapping.as("m"), lower($"sc.servicecompany") === lower('wv_vendor_name), "left").
      join(afeToJobReportDS.as("afecost"), $"afecost.idrecjob" === $"npt.idrecparent" && $"npt.idrecjobprogramphasecalc" === $"afecost.idrecjobprogramphasecalc" &&  $"wh.idwell" === $"afecost.idwell", "left").
      join(jobRepTimeLogToJobReport, $"npt.idrec" === $"idrecJobIntervalProblem", "left").
      join(nptDetailCodeMapping_.as("ndc"), $"npt.typ" === $"ndc.npt_type" && $"npt.typdetail" === $"ndc.npt_type_detail", "left").
      select('servicetyp, $"npt.idwell", $"npt.idrec".as("idrecntervalProblem"), $"npt.idrecparent".as("job_id"),
      'rigno, 'npt_date_start, 'npt_date_end, 'durationgrosscalc.cast("Double").as("duration"),
      coalesce('parent_name, 'servicecompany).as("parent_name"), $"npt.refno", $"npt.status", $"npt.typ", 'typdetail,
      'costcalc.cast(DoubleType).as("cost"), $"npt.com", lit(null: String).as("internalProblemType"), $"npt.idrecjobprogramphasecalc",
      'estcostoverride.as("estcostoverride_str"), 'afenumber, 'npt_type_detail_description, 
      'npt_type_typedetail_concat, $"afecost.jobreport_date", $"afecost.idrecjob", $"gl_code",
      'costgen_des, 'activitydescr).
      withColumn("afenumber", trim(regexp_replace('afenumber,"-",""))).
      withColumn("jobreport_date", unixTimeToStringDate($"afecost.jobreport_date")).
      withColumn("gl_code_str", concat_ws(", ", $"gl_code")).
      withColumn("estcostoverride", coalesce('estcostoverride_str.cast("Double"), lit(0))).
      withColumn("group_refno", split('refno, "\\.")(0)).
      withColumn("interval_problem_id", getLongHashCodeUDF(concat_ws("", split('refno, "\\.")(0), 'job_id))).
      withColumn("npt_type_detail_description", remove_specialchar_udf($"npt_type_detail_description")).
      where("afenumber is not null").
      as[JobIntervalProblem].map(rec => rec.copy(internalProblemType = calculateInternalProblemType(rec))).
      filter('internalProblemType =!= InternalProblemType.NOT_DISCOUNTED && year($"npt_date_start") >= 2015).distinct()

//val intervalProblemDS = intervalProblemDS.withColumn("npt_type_detail_description", remove_specialchar_udf($"npt_type_detail_description"))

//remove_specialchar_udf

// withColumn("npt_type_detail_description", remove_specialchar_udf('npt_type_detail_description)).
      
// println(intervalProblemDS.where('parent_name === Config.CHEVRON_VENDOR_NAME).count() + "<== intervalProblemDS")

// println(intervalProblemDS.where('parent_name === Config.CHEVRON_VENDOR_NAME).distinct())

// intervalProblemDS.write.parquet("/user/svccdfpd/tmp/intervalProblemDS_192020_1329.parquet")
   
   val isAppliedUDF = udf(isApplied)
   intervalProblemDS.as("ip").
     joinWith(wvCostDetail.as("cd"), $"ip.job_id" === $"cd.job_id"
       && $"ip.idrecntervalProblem" === $"cd.idrec"
       && $"ip.afenumber" === $"cd.afenumber"
       && to_date($"ip.npt_date_start") === to_date($"cd.nptstart_date")
       && isAppliedUDF($"ip.internalProblemType",$"ip.parent_name",$"cd.parent_name"), "left").
       as[(JobIntervalProblem, CostDetail)] 
    

  }
}
