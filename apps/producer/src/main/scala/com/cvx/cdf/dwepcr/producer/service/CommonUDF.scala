package com.cvx.cdf.dwepcr.producer.service

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{to_date, to_utc_timestamp, udf}

import scala.annotation.tailrec

object CommonUDF  extends Serializable{
  private val multiplyVectors = (v1: org.apache.spark.ml.linalg.Vector, v2: org.apache.spark.ml.linalg.Vector) => {
    v1.toArray.zip(v2.toArray).map(rec => rec._1 * rec._2).sum
  }
  val multiplyVectorsUdf = udf(multiplyVectors)

  private val cosineSimilarity = (vectorA: org.apache.spark.ml.linalg.Vector, vectorB: org.apache.spark.ml.linalg.Vector) => {
    val x = vectorA.toArray
    val y = vectorB.toArray
    val dotProduct = (x: Array[Double], y: Array[Double]) => {
      (for ((a, b) <- x zip y) yield a * b) sum
    }
    val magnitude = (x: Array[Double]) => {
      math.sqrt(x map(i => i*i) sum)
    }
    require(x.size == y.size)
    if(magnitude(x) * magnitude(y) == 0)
      0d
    else
      dotProduct(x, y)/(magnitude(x) * magnitude(y))
  }

  val cosineSimilarityUdf = udf(cosineSimilarity)


  val jaccardSimilarity = (col1: Seq[String], col2: Seq[String]) => {
    val set_1 = col1.toSet
    val set_2 = col2.toSet
    val union = set_1.union(set_2)
    val intersection = set_1.intersect(set_2)
    intersection.size.toDouble / union.size.toDouble
  }

  val jaccardSimilarityUdf = udf(jaccardSimilarity)

  val produceNGramUDF = udf((str: Seq[String], n:Int) => str.sliding(n,1).map(rec => rec.mkString(" ")).toSeq)

  val flatterStrSeqUDF = udf((seqOfSeq: Seq[Seq[String]]) => seqOfSeq.flatten)

  val produceNGramFactUDF = udf((str: Seq[String], n:Int) => {
    str ++ (2 to n).
      flatMap(cn => str.sliding(cn,1)).map(_.mkString(" "))
  })

  def unixTimeToDateTime(ut : org.apache.spark.sql.Column) = {
    to_utc_timestamp(ut.divide(1000).cast("timestamp"), "CST")
  }

  def unixTimeToStringDate(ut : org.apache.spark.sql.Column) = {
    to_date(unixTimeToDateTime(ut)).cast("String")
  }

  val getFirstCharUDF = udf((str: String, len: Int) =>
    if(str != null) str.substring(0, Math.min(len, str.length)).toUpperCase else "")

  val dict = (('A' to 'Z') ++ ('0' to '9') ++ ('a' to 'z')).zipWithIndex.map(_.swap).toMap
  @tailrec
  def toTextNumber(restVal: Long, resVal: List[Char]): List[Char] ={
    if(restVal == 0){
      return resVal
    }else{
      return toTextNumber(restVal / dict.size, dict((restVal % dict.size).toInt) :: resVal)
    }
  }

  val getHashId:String => Option[String] = (str: String) => {
    if(str == null){
      None
    }else {
      val hash = str.toCharArray.foldLeft(0L)((x1, x2) => 31 * x1 + x2)

      Some(toTextNumber(Math.abs(hash), Nil).mkString)
    }
  }

  val getHashIdUDF = udf(getHashId)

  val getLongHashCode:String => Option[Long] = (str: String) => {
    if(str == null){
      None
    }else {
      Some(str.toCharArray.foldLeft(0L)((x1, x2) => 31 * x1 + x2))
    }
  }

  val getLongHashCodeUDF = udf(getLongHashCode)

//val remove_specialchar: String => String = _.replaceAll("â€“~ûùíáüÞ", "")

  val remove_specialchar: String => String = (str: String) => {
    if(str != null) str.replaceAll("â€“~ûùíáüÞ", "") else null
  }

  val remove_specialchar_udf = udf(remove_specialchar)


}
