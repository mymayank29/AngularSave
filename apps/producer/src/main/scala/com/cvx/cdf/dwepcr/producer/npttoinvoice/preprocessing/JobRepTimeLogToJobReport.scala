package com.cvx.cdf.dwepcr.producer.npttoinvoice.preprocessing

import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager
import com.cvx.cdf.dwepcr.producer.service.{CommonUDF, SparkConnector}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object JobRepTimeLogToJobReport extends Serializable{
  import SparkConnector.spark
  import spark.implicits._
  import CommonUDF._

  lazy val getJobRepTimeLogToJobReport: DataFrame = {
    val preparedMapping = DataManager.get.activityCodeMapping.
      withColumn("joined_codes", concat_ws("", 'code1, 'code2))

    val jobReportTimelog = DataManager.get.wvJobReportTimelog.
      withColumn("npt_num", explode(split('refnoproblemcalc, ",")))

    jobReportTimelog.as("jobreptimelog").withColumn("time_log_id", 'idrec).
      join(DataManager.get.wvJobReport.as("jobrep"), $"jobreptimelog.idrecparent" === $"jobrep.idrec").
      withColumn("id_job", $"jobrep.idrecparent").
      join(preparedMapping.as("mapping"), $"jobreptimelog.code1" === $"mapping.activitycode", "left").
      groupBy('id_job, 'time_log_id, 'activitycode, 'com, 'duration,
        unixTimeToStringDate('dttmendcalc).as("end_date"),
        unixTimeToStringDate('dttmstartcalc).as("start_date"),
        $"jobrep.idrecparent".as("job_id_timelog"),
        $"jobreptimelog.code1",
        unixTimeToStringDate('dttmstart).as("jobreport_date_timelog"),
        $"npt_num").
      agg(collect_set($"mapping.joined_codes").as("gl_code"),
        concat_ws(". ", collect_set('costgen_des)).as("costgen_des"),
        concat_ws(". ", collect_set('activitydescr)).as("activitydescr"))
  }
}
