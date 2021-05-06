package com.cvx.cdf.dwepcr.producer.mlpreprocessing

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object TextToSentenceTransformer   extends Serializable {
  val MIN_SENTENCE_SIZE = 30

  val splitSentence = (inputColumn: String) => {
    val spletedWithSize = inputColumn.split("(?<=[a-zA-Z0-9][^((No.)|(N0.))][!?.])\\s+").map(rec => (rec, rec.size))
    spletedWithSize.scanLeft("",0)((prev, cr) => (cr._1, cr._2 + prev._2)).drop(1).
      map(rec => (rec._1, rec._2 / MIN_SENTENCE_SIZE)).
      groupBy(_._2).map(_._2.map(_._1).mkString(" ")).
      zipWithIndex.toList
  }

  val splitSentenceUDF = udf(splitSentence)

  def transform(sourceDF: DataFrame, inputColumn: String, outputColumn: String): DataFrame = {
    sourceDF.
      withColumn(outputColumn, explode(splitSentenceUDF(col(inputColumn)))).
      withColumn(s"${outputColumn}_line", col(s"${outputColumn}._2")).
      withColumn(s"${outputColumn}", col(s"${outputColumn}._1"))
  }

  val test = "12345678 12 1 12345 3 4567 5 6 7889".split(" ").map(rec => (rec, rec.size))
  test.scanLeft("",0)((prev, cr) => (cr._1, cr._2 + prev._2)).drop(1).
    map(rec => (rec._1, rec._2 / 10)).
    groupBy(_._2).map(_._2.map(_._1).mkString(" "))
//  test.map(rec => (rec, rec.size)).re

  val integers: List[Int] = List(1,3,4,5)
  // scanLeft produces:  List(0, 1, 4, 8, 13)
  val partialSum1 =  integers.scanLeft(0)(_ + _)
  //> Same trick, but then take the tail to get the values you want:List(1, 4, 8, 13)
  val partialSum2 =  (integers.scanLeft(0)(_ + _)).tail
}