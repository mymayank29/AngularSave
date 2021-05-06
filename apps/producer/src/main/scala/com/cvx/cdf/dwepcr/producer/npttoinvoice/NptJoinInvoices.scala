package com.cvx.cdf.dwepcr.producer.npttoinvoice

import com.cvx.cdf.dwepcr.producer.mlpreprocessing.TextSimilarityComparator
import com.cvx.cdf.dwepcr.producer.service.Config._
import com.cvx.cdf.dwepcr.producer.npttoinvoice.models.{InvoiceToNptLink, _}
import com.cvx.cdf.dwepcr.producer.npttoinvoice.models.enumaration.InternalProblemType
import com.cvx.cdf.dwepcr.producer.service._
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManagerDB
import com.cvx.cdf.dwepcr.producer.npttoinvoice.preprocessing.{AfeDetail, InvoicePreprocessor, JobIntervalProblemPreprocessor, JobRepTimeLogToJobReport}
import com.cvx.cdf.dwepcr.producer.service.{Config, SparkConnector}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.catalyst.plans.JoinType
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object NptJoinInvoices extends Logging {
  logger.info(s"[Spark] npt join invoices start.")

  import SparkConnector.spark
  import spark.implicits._
  import NptJoinInvoicesFunctions._
  import CommonUDF._
  import TextSimilarityComparator._

   val invoices = InvoicePreprocessor.invoices.coalesce(10).cache()
   val internalProblemWithDependency: Dataset[(JobIntervalProblem, CostDetail)] = JobIntervalProblemPreprocessor.getInternalProblemWithDependency.coalesce(10).cache()
   val afeDetail = AfeDetail.afeDetail.coalesce(10).cache()

// there are 4011 records where the below conditions are not met
//interval_problem_details has 70k approx records and invoice_for_full_sf has 10k approx records because invoice is like aggregating the things

   val intervalProblemRelatedInvoices = internalProblemWithDependency.
     joinWith(invoices, $"_2.afenumber" === $"wbs_element_id" && $"_2.parent_name" === $"parent_vendor"
       && $"_1.npt_date_start" < $"submit_date", "left").
    select($"_1._1".as("jobIntervalProblem"), $"_1._2".as("costDetail"), $"_2".as("invoice")).toDF().cache

//    intervalProblemRelatedInvoices.write.parquet("/user/svccdfpd/tmp/intervalProblemRelatedInvoices_192020_1329.parquet")

  //  TODO - these 2 commands train models and save them - so they should be executed only once. We need to think how to implement this
  //  TextSimilarityComparator.produceModel(joined,"jobIntervalProblem.servicetyp", "invoice.contract_title_no_vendor", "contract_to_npt_model")
  //  TextSimilarityComparator.produceContractToCostGenSimilarityModel(joined, "jobIntervalProblem.costgen_des",
  //    "invoice.contract_title_no_vendor", "contract_to_costgen_model")

   val intervalProblemRelatedInvoicesWithSimilarity = {
    val mapping = DataManagerDB.activityToContractsExceptionsMapping.
      withColumn("npt_activ_array", split(col("Related_Activity_During_NPT"), ";")).
      select('Contract_ID, 'npt_activ_array).as[(String, Array[String])].collect().toMap.map(identity)

    val exceptionalMatchesDictionary = spark.sparkContext.broadcast(mapping)

    intervalProblemRelatedInvoices.
      transform(findSimilarity(_, "jobIntervalProblem.servicetyp", "invoice.contract_title_no_vendor", "cosSimNPT", "contract_to_npt_model")).
      transform(findContractToCostGenSimilarity(_, "jobIntervalProblem.costgen_des", "invoice.contract_title_no_vendor", "cosSimCostDescr", "contract_to_costgen_model")).
      transform(findInvoiceDescToNPTCommentSimilarity(_, "invoice.description", "jobIntervalProblem.com", "cosSimNPT", "cosSimCostDescr", "invoice_to_npt_descr_match")).
      as[(JobIntervalProblem, CostDetail, Invoice, Double, Double, Double)].
      map(rec => (InvoiceNpt(rec._1, rec._2, rec._3), evaluate(rec, exceptionalMatchesDictionary.value))).cache()
  }

  def margeIntervalProblem(ds: Dataset[(JobIntervalProblem, CostDetail)]):Dataset[JobIntervalProblem] = {
    ds.groupByKey(_._1.interval_problem_id).mapGroups((key, values) => {
      val jobIntervalProblemSeq = values.map(_._1).toList.groupBy(_.idrecntervalProblem).mapValues(_.head).values.toList
      val distinctRecNpt = jobIntervalProblemSeq.sortBy(_.idrecntervalProblem).head
      JobIntervalProblem(
        afenumber = distinctRecNpt.afenumber,
        interval_problem_id = distinctRecNpt.interval_problem_id,
        servicetyp = distinctRecNpt.servicetyp,
        rigno = distinctRecNpt.rigno,
        idwell = distinctRecNpt.idwell,
        idrecntervalProblem = distinctRecNpt.idrecntervalProblem,
        job_id = distinctRecNpt.job_id,
        npt_date_start = safeMin(jobIntervalProblemSeq.map(_.npt_date_start)),
        npt_date_end = safeMax(jobIntervalProblemSeq.map(_.npt_date_end)),
        parent_name = distinctRecNpt.parent_name,
        cost = jobIntervalProblemSeq.map(_.cost).sum,
        estcostoverride = jobIntervalProblemSeq.map(_.estcostoverride).sum,
        status = distinctRecNpt.status,
        refno = distinctRecNpt.group_refno,
        duration = Some(jobIntervalProblemSeq.map(_.duration).foldLeft(0.0)((acc, optNum) => acc + optNum.getOrElse(0.0))),
        typ = distinctRecNpt.typ,
        typdetail = distinctRecNpt.typdetail,
        com = jobIntervalProblemSeq.map(_.com).filter(_ != null).distinct.sorted.mkString(" ."),
        internalProblemType = distinctRecNpt.internalProblemType,
        gl_code = jobIntervalProblemSeq.filter(_.gl_code != null).flatMap(_.gl_code).toSet.filter(_ != null).toArray.sorted,
        gl_code_str = jobIntervalProblemSeq.filter(_.gl_code != null).flatMap(_.gl_code_str.split(",")).filter(_ != null).distinct.sorted.mkString(","),
        costgen_des = jobIntervalProblemSeq.map(_.costgen_des).filter(_ != null).distinct.sorted.mkString("."),
        activitydescr = jobIntervalProblemSeq.map(_.activitydescr).filter(_ != null).distinct.sorted.mkString("."),
        npt_type_detail_description = jobIntervalProblemSeq.map(_.npt_type_detail_description).filter(_ != null).distinct.sorted.mkString("."),
        npt_type_typedetail_concat = distinctRecNpt.npt_type_typedetail_concat,
        group_refno = distinctRecNpt.group_refno
      )
    })
  }

  val intervalProblemGrouped = internalProblemWithDependency.filter(_._2 != null).transform(margeIntervalProblem)
  val intervalProblemDetails = intervalProblemRelatedInvoicesWithSimilarity
    .map(rec => (rec._1.jobIntervalProblem, rec._1.costDetail,  rec._2))  //DEBUG - Add cost detail parent name to intervalProblemDetails results
    .select($"_1.*", $"_2.parent_name".as("cost_parent_name"), $"_2.code", $"_3.ariba_doc_id", $"_3.byCode", $"_3.byDate") // removed on 192020 @ 158pm testing.filter("ariba_doc_id is not null")

  val intervalProblemGroupByRefNo = intervalProblemRelatedInvoicesWithSimilarity.filter(_._1.costDetail != null)
    .map(rec => {
      ((rec._1.jobIntervalProblem.interval_problem_id, rec._2.ariba_doc_id ),
        (rec._1, rec._2))
    })
    .groupByKey(rec => rec._1)
    .mapGroups((key, value) => {
      val values = value.toSeq.map(_._2)
      val invoiceNptLinkSeq = values.map(_._2)

      val invoiceToNptLink:InvoiceToNptLink = InvoiceToNptLink(
        ariba_doc_id = key._2,
        npt_sub_group_duration = invoiceNptLinkSeq.map(_.npt_sub_group_duration).sum,
        npt_sub_group_duration_bydate_only = invoiceNptLinkSeq.map(_.npt_sub_group_duration_bydate_only).sum,
//        cost_gen_id = invoiceNptLinkSeq.map(_.cost_gen_id).head,
        interval_problem_id = key._1,
        byCode = invoiceNptLinkSeq.map(_.byCode).max,
        byDate = invoiceNptLinkSeq.map(_.byDate).max,
        byTitle = invoiceNptLinkSeq.map(_.byTitle).max,
        byGlCode = invoiceNptLinkSeq.map(_.byGlCode).max,
        byGlCodePercents = invoiceNptLinkSeq.map(_.byGlCodePercents).max,
        byTitleImpr = invoiceNptLinkSeq.map(_.byTitleImpr).sorted.reverse.head,
        byTitleImprPercents = invoiceNptLinkSeq.map(_.byTitleImprPercents).max,
        byInvoiceDescription = invoiceNptLinkSeq.map(_.byInvoiceDescription).sorted.reverse.head,
        isExceptionalMatchCase = invoiceNptLinkSeq.map(_.isExceptionalMatchCase).max,
        isEmptyDate = invoiceNptLinkSeq.map(_.isEmptyDate).max
      )
      invoiceToNptLink
    })

    //looks like ciaInvoiceToNptLinkDF this or the cia has an issue or 0 records. But why ?


   val ciaInvoiceToNptLinkDF = intervalProblemGroupByRefNo.
    withColumn("id", getHashIdUDF(concat(coalesce('ariba_doc_id, lit("")),'interval_problem_id.cast("String")))).cache()

//Check this below b)	ciaIntervalProblemDF --> What does this do ?? 
   val ciaIntervalProblemDF = intervalProblemGrouped.
    withColumn("VendorAndWellNumber", row_number().over(Window.partitionBy('idwell, 'parent_name).orderBy('interval_problem_id))).
    withColumn("WetherNumber", row_number().over(Window.partitionBy('idwell).orderBy('interval_problem_id))).
    withColumn("npt_event_no", when('internalProblemType =!= InternalProblemType.DISCOUNT_ALL, concat(getFirstCharUDF('parent_name, lit(4)), 'VendorAndWellNumber)).
      otherwise(concat(lit("Wthr"), 'WetherNumber))).
    withColumn("isWether", 'internalProblemType === InternalProblemType.DISCOUNT_ALL).
    drop('VendorAndWellNumber).
    drop('WetherNumber).cache()

  val durationTotalColumn = when('byDate || 'byCode || 'bytitle, 'npt_sub_group_duration).otherwise(lit(0))
  val durationTotalRelatedColumn = when('byDate, 'npt_sub_group_duration_bydate_only).otherwise(lit(0))
  val spendLeakageDF = ciaInvoiceToNptLinkDF.join(ciaIntervalProblemDF, "interval_problem_id").
    groupBy('ariba_doc_id, 'isWether).agg(
    sum(durationTotalColumn).as("npt_duration_total"),
    sum(durationTotalRelatedColumn).as("npt_duration_date_related"))

  val ciaInvoiceDF = intervalProblemRelatedInvoices.
    filter($"invoice".isNotNull).select($"invoice.*").distinct()

  val ciaInvoiceSpentLeakageByIsWeatherDF = ciaInvoiceDF.
    join(spendLeakageDF, Seq("ariba_doc_id")).
    withColumn("duration_total", (datediff('inv_end_date, 'inv_start_date) + 1) * 24).
    withColumn("cost_per_hour", 'price.cast("Double") / 'duration_total).
    withColumn("pt_spent_leakage_total", round(least('cost_per_hour * 'npt_duration_total,'price))).drop("npt_duration_total").
    withColumn("pt_spent_leakage_date_related", round(least('cost_per_hour * 'npt_duration_date_related,'price))).
    withColumn("afe",'wbs_element_id).withColumn("afenumber",'wbs_element_id)

  val ciaInvoiceToNptLinkDetailedDF = ciaInvoiceToNptLinkDF.
    join(ciaIntervalProblemDF, "interval_problem_id").
    join(ciaInvoiceSpentLeakageByIsWeatherDF, Seq("afenumber", "ariba_doc_id","isWether"), "left").
    withColumn("pt_spent_leakage_npt", when('bydate, round(least('cost_per_hour * 'npt_sub_group_duration_bydate_only, 'price))).otherwise(round(least('cost_per_hour * 'npt_sub_group_duration, 'price)))).cache()

  val jobRepTimeLogToJobReport = JobRepTimeLogToJobReport.getJobRepTimeLogToJobReport

  val timeLogWindow = Window.partitionBy('time_log_id)
  val timeLogDetail = jobRepTimeLogToJobReport.as("tl").
    join(ciaIntervalProblemDF.as("ip"), $"tl.id_job" === $"ip.job_id" && $"tl.start_date" === to_date($"ip.npt_date_start")).
    withColumn("is_npt", $"tl.npt_num" === $"ip.refno").
    select('interval_problem_id, 'activitycode,$"tl.activitydescr" ,$"tl.costgen_des",$"tl.com",$"tl.duration",'end_date,'start_date,
      'job_id_timelog,'code1,'jobreport_date_timelog,'npt_num,
      concat_ws("; ", collect_set('npt_event_no).over(timeLogWindow)).as("npt_event_no"),
      max("is_npt").over(timeLogWindow).as("is_npt")).
    withColumn("npt_event_no", when('is_npt, 'npt_event_no).otherwise(lit("")))

//TODO deprecat from UI
    val invoice_view = ciaInvoiceToNptLinkDF.join(ciaIntervalProblemDF, "interval_problem_id").
      select('id, 'afenumber,'job_id, 'idwell, 'npt_event_no, 'ariba_doc_id, 'isWether, 'servicetyp, 'npt_event_no.as("npt_no"), 'npt_sub_group_duration.as("npt_duration"), 'npt_sub_group_duration_bydate_only.as("npt_duration_bydate"),
        'interval_problem_id, 'byDate, 'byCode, 'bytitle, 'byGlCode, 'byGlCodePercents, 'byTitleImpr, 'byTitleImprPercents, 'isExceptionalMatchCase, 'byInvoiceDescription, 'isEmptyDate,
        'npt_date_start, 'npt_date_end, 'refno, 'rigno, 'npt_type_typedetail_concat, 'com.as("npt_com"), 'npt_type_detail_description).
      withColumn("numbers", concat_ws(";", collect_set(when('byDate === true, 'npt_event_no).otherwise(lit(null: String))).
        over(Window.partitionBy('ariba_doc_id, 'isWether)))).
      join(ciaInvoiceSpentLeakageByIsWeatherDF, Seq("afenumber", "ariba_doc_id","isWether")).
      drop(ciaInvoiceDF("ariba_doc_id")).
      withColumn("pt_spent_leakage_npt", when('bydate, round(least('cost_per_hour * 'npt_duration_bydate, 'price))).otherwise(round(least('cost_per_hour * 'npt_duration, 'price)))).
      withColumn("code", concat_ws(", ", 'code)).distinct().cache()
      
//   val invoice_view = ciaInvoiceToNptLinkDF.join(ciaIntervalProblemDF, "interval_problem_id").
//    select('id, 'afenumber,'job_id, 'idwell, 'npt_event_no, 'ariba_doc_id, 'isWether, 'servicetyp, 'npt_event_no.as("npt_no"), 'npt_sub_group_duration.as("npt_duration"),
//      'interval_problem_id, 'byDate, 'byCode, 'bytitle, 'byGlCode, 'byGlCodePercents, 'byTitleImpr, 'byTitleImprPercents, 'isExceptionalMatchCase, 'byInvoiceDescription, 'isEmptyDate,
//      'npt_date_start, 'npt_date_end, 'refno, 'rigno, 'npt_type_typedetail_concat, 'com.as("npt_com"), 'npt_type_detail_description).
//    withColumn("numbers", concat_ws(";", collect_set(when('byDate === true, 'npt_event_no).otherwise(lit(null: String))).
//      over(Window.partitionBy('ariba_doc_id, 'isWether)))).
//    join(ciaInvoiceSpentLeakageByIsWeatherDF, Seq("afenumber", "ariba_doc_id","isWether")).
//    drop(ciaInvoiceDF("ariba_doc_id")).
//    withColumn("pt_spent_leakage_npt", round('cost_per_hour * 'npt_duration)).
//    withColumn("code", concat_ws(", ", 'code)).distinct().cache()

  //todo
   val invoiceForFullSf = invoice_view.
    join(afeDetail, Seq("afe", "idwell", "job_id")).dropDuplicates("id")

  //todo
  val intervalProblemFullSf = ciaIntervalProblemDF.withColumn("afe", 'afenumber).join(afeDetail.drop("idwell"), Seq("afe", "job_id")).distinct()

  val intervalProblemFullSfWithLink = intervalProblemFullSf.join(ciaInvoiceToNptLinkDF, "interval_problem_id")

  logger.info(s"[Spark] npt join invoices end.")

  def ciaInvoiceToNptLinkDetailedView = ciaInvoiceToNptLinkDetailedDF.drop("code").toDF()
  def ciaInvoiceDFView = invoice_view.drop("code").toDF()
  def ciaIntervalProblemDFView = ciaIntervalProblemDF.drop("gl_code")
  def ciaInvoiceToNptLinkDFView = ciaInvoiceToNptLinkDF
  def invoiceForFullSfView = invoiceForFullSf.distinct()
  def intervalProblemFullSfView = intervalProblemFullSf.drop("gl_code")
  def intervalProblemDetailsView = intervalProblemDetails.distinct()
  def afeDetailView = afeDetail
  def intervalProblemFullSfWithLinkView = intervalProblemFullSfWithLink.drop("gl_code")
  def timeLogDetailView = timeLogDetail
}