package com.cvx.cdf.dwepcr.producer.contract.analyzer

import com.cvx.cdf.dwepcr.producer.mlpreprocessing.PorterStemmer
import com.cvx.cdf.dwepcr.producer.service.SparkConnector

import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame}

object TextCleaner extends Serializable {

  val sentenceCleaner = (str: String) => {
    if(str == null)
      Array[String]()
    else
      str.
        toLowerCase.
        replaceAll("\\.", "").
        replaceAll(",", "").
        replaceAll("\\s+", " ").
        replaceAll("\\p{P}+", " ").
        split(" ").
        map(PorterStemmer.stem).
        filter(word => !word.isEmpty && word.length>1)
  }

  val stringStemmerUDF = udf(sentenceCleaner)

  def transform(df: DataFrame, inputColumn: String, outputColumn: String):DataFrame = {

    val tempCol1 = s"${inputColumn}_1"
    val tempCol2 = s"${inputColumn}_2"
    val stemmed = df.withColumn(tempCol1, stringStemmerUDF(new Column(inputColumn)))
    val remover = new StopWordsRemover().setInputCol(tempCol1).setOutputCol(tempCol2)
    stemmed.
      transform(remover.transform).withColumn(outputColumn, col(tempCol2)).
      drop(tempCol1, tempCol2)
  }
}
