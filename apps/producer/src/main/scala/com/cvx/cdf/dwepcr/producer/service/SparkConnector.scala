package com.cvx.cdf.dwepcr.producer.service

import org.apache.spark.sql.{SparkSession}

object SparkConnector extends Serializable{
  val spark = SparkSession.builder.getOrCreate()
  @transient val sparkContext = spark.sparkContext
}