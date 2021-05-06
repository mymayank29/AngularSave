package com.cvx.cdf.dwepcr.producer.contract.analyzer

import com.cvx.cdf.dwepcr.producer.service.SparkConnector
import com.cvx.cdf.dwepcr.producer.service.SparkConnector.spark

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.regexp_replace
import org.apache.spark.sql.functions._

object TestDataExcelReader extends Serializable {
  import SparkConnector.spark
  import spark.implicits._

  def readAsDF(file: String): DataFrame =
    spark.read.option("header", "true").csv(file)

  def filesForTraining = readAsDF(com.cvx.cdf.dwepcr.producer.service.Config.amendmentsTrainDataPath).
    toDF("path","is_amendment").
    withColumn("path", regexp_replace('path,"\\\\","/")).
    withColumn("is_amendment", when('is_amendment === "TRUE", 1).otherwise(0))
}
