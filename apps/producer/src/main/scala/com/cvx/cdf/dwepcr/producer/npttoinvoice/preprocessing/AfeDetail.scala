package com.cvx.cdf.dwepcr.producer.npttoinvoice.preprocessing

import com.cvx.cdf.dwepcr.producer.get_first
import com.cvx.cdf.dwepcr.producer.npttoinvoice.models.Invoice
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager
import com.cvx.cdf.dwepcr.producer.service.{CommonUDF, SparkConnector}
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.functions._

import math.Ordering
object AfeDetail extends Serializable{
  import SparkConnector.spark
  import spark.implicits._
  import CommonUDF._

  def afeDetail:DataFrame = {
    DataManager.get.wvWellHeader.
      join(DataManager.get.wvJob, "idwell").as("j").
      join(DataManager.get.wvJobAfe.as("a"), $"j.idrec"===$"a.idrecparent").
      select(
        $"j.idwell",
        $"j.idrec".as("job_id"),
        'wellname,
        trim(regexp_replace('afenumber,"-","")).as("afe"),
        'jobtyp,
        unixTimeToStringDate('dttmstart).as("wv_job_start_date"),
        unixTimeToStringDate('dttmend).as("job_end_date")
    )
  }
}
