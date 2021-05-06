package com.cvx.cdf.dwepcr.producer.mlpreprocessing
import com.cvx.cdf.dwepcr.producer.service.SparkConnector
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._


object NGramPopulator extends Serializable{
  import SparkConnector.spark
  import spark.implicits._

  def transform(sourceDF: DataFrame, inputColumn: String, outputColumn: String, n: Int): DataFrame = {
    sourceDF.withColumn(outputColumn, col(inputColumn))
  }
}
