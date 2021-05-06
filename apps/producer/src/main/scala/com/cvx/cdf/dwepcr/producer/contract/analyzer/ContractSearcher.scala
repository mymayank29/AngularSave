package com.cvx.cdf.dwepcr.producer.contract.analyzer

import java.util.regex.Pattern

import com.cvx.cdf.dwepcr.producer.service.{Config, SparkConnector}
import org.apache.spark.sql.functions._

object ContractSearcher {
  def process()= {
    import SparkConnector.spark
    import spark.implicits._
    import TestDataExcelReader._
    import CommonFunctions._
    spark.sparkContext.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive","true")

    val filesDF = spark.sparkContext.wholeTextFiles(Config.ocrResultPath, 100).
      toDF("path", "content").
      withColumn("path", regexp_replace('path,s".*?${Config.ocrResultPath}","")).
      withColumn("path", getRelatedPathUDF('path)).
      withColumn("content", lower(substring('content, 0, 3000))).
      withColumn("contains_amendment", containsAmendmentUDF('content)).
      withColumn("non_amendment_words", nonAmendmentWordsUDF('path, 'content)).
      transform(TextCleaner.transform(_, "content", "content")).
    cache()

    val trainingFiles = filesDF.join(filesForTraining, "path").cache()

    val pipeLine = createPipeline("content", "is_amendment")

    val model = pipeLine.fit(trainingFiles)

    val allFiles = filesDF.join(filesForTraining, Seq("path"), "left").
      withColumn("is_amendment_manual", 'is_amendment).drop('is_amendment).cache()

    val filesWithAmendmentPrediction = model.transform(allFiles).
      drop("vector_v1","features","rawPrediction","probability").cache()

    val finalResult = filesWithAmendmentPrediction.
      withColumn("is_amendment_manual", 'is_amendment_manual === 1).
      withColumn("prediction", 'prediction === 1).
      withColumn("is_amendment", coalesce('is_amendment_manual, 'prediction && 'contains_amendment && !'non_amendment_words)).
      withColumn("date_From_Title", unix_timestamp(dateFromTitleUDF('path), "yyyy/MM/dd")).
      withColumn("date_From_Text", unix_timestamp(dateFromTextUDF('content), "yyyy/MM/dd")).
      withColumn("contract_id", contractNumberFromPathUDF('path)).drop("content")

    finalResult
  }
}
