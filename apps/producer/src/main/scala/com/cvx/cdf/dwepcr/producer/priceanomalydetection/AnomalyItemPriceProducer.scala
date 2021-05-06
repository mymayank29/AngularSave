package com.cvx.cdf.dwepcr.producer.price

import com.cvx.cdf.dwepcr.producer.priceanomalydetection.{AnomalyItemPriceDetectorDescrMatch, AnomalyItemPriceDetectorPartialMatch}
import com.cvx.cdf.dwepcr.producer.service.SparkConnector
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager
import org.apache.spark.sql.functions._

object AnomalyItemPriceProducer {


  import SparkConnector.spark
  import spark.implicits._
  import com.cvx.cdf.dwepcr.producer.priceanomalydetection.Functions._

  def produceAnomalyItemGroups = {

    val overcharge_detail = unionAll(AnomalyItemPriceDetectorPartialMatch.calculatePotentialOvercharge,
      AnomalyItemPriceDetectorDescrMatch.calculatePotentialOvercharge).as("price_anomaly")

    val contractsWithAmendments = DataManager.get.contractsWithAmendments
      .groupBy('contract_id)
      .agg(collect_set($"date_From_Title").as("dates_from_title"),
        collect_set($"date_From_Text").as("dates_from_text"))
      .as("amendments")

    val resultDF = overcharge_detail
      .join(contractsWithAmendments, Seq("contract_id"), "left")
      .withColumn("start_date_timestamp", unix_timestamp(parseDateFromIntUDF('start_date), "yyyy/MM/dd"))
      .withColumn("amendment_date", getMostRecentDateUDF_('start_date_timestamp, 'dates_from_title, 'dates_from_text))
      .withColumn("amendment_date_readable", from_unixtime('amendment_date))
      .select(overcharge_detail("*"), 'amendment_date, 'amendment_date_readable)

    resultDF
  }

}
