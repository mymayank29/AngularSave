package com.cvx.cdf.dwepcr

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.functions.udf

package object producer  extends Serializable{
  def vecPow = (vector: org.apache.spark.ml.linalg.Vector) => {
    val distanceDuffer: scala.collection.mutable.ArrayBuffer[(Int, Double)] = scala.collection.mutable.ArrayBuffer()
    vector.foreachActive((k, v) => {
      distanceDuffer += ((k, Math.pow(v, 3)))
    })

    Vectors.sparse(vector.size, distanceDuffer)
  }

  val distance = (v1: org.apache.spark.ml.linalg.Vector, v2: org.apache.spark.ml.linalg.Vector) => {
    val vector1 = vecPow(v1)
    val vector2 = vecPow(v2)
    val zeros = Vectors.zeros(v1.size)
    Vectors.sqdist(vector1,vector2) / Math.min(Vectors.sqdist(zeros,vector1), Vectors.sqdist(vector2,zeros))
  }
  val distanceUdf = udf(distance)

  val wordWeight = (v: org.apache.spark.ml.linalg.Vector) => {
    val vector = vecPow(v)
    vector.toArray.max + vector.toArray.map(_ / 10).sum
  }
  val wordWeightUdf = udf(wordWeight)

  val distanceToZero = (v1: org.apache.spark.ml.linalg.Vector, v2: org.apache.spark.ml.linalg.Vector) => {
    val zeros = Vectors.zeros(v1.size)
    Math.min(Vectors.sqdist(zeros,vecPow(v2)), Vectors.sqdist(vecPow(v1),zeros))
  }
  val distanceToZeroUdf = udf(distanceToZero)

  def normalizeCode = (str:String) => {
    try {
      (1 to (4 - str.size)).map(_ => '0').mkString + str
    } catch {
      case e: Exception => ""
    }
  }
  val normalizeCodeUDF = udf(normalizeCode)

  val get_first = udf((xs: Seq[String]) => if(xs != null && xs.size>0) xs(0) else "")


  def tokenizer = (str: String) => {
    str.
      replaceAll("[ ]?&[ ]?", "___").
      toLowerCase.
      replaceAll("[^a-z _]", " ").
      split(" ").
      map(_.trim).
      filter(!_.isEmpty).
      toList
  }
  val tokenizerUDF = udf(tokenizer)

  def extractMonth(str: String) = {
    if(str.forall(_.isDigit)) {
      if(str.length <= 2 && str.toInt <= 12) Some(toTwoChars(str)) else None
    } else str match {
      case u if u.startsWith("ja") => Some("01")
      case u if u.startsWith("f") => Some("02")
      case u if u.startsWith("mar") => Some("03")
      case u if u.startsWith("ap") => Some("04")
      case u if u.startsWith("ma") => Some("05")
      case u if u.startsWith("jun") => Some("06")
      case u if u.startsWith("jul") => Some("07")
      case u if u.startsWith("au") => Some("08")
      case u if u.startsWith("s") => Some("09")
      case u if u.startsWith("o") => Some("10")
      case u if u.startsWith("n") => Some("11")
      case u if u.startsWith("d") => Some("12")
      case _ => None
    }
  }

  def toTwoChars(str:String) = {
    if(str.length >= 2) str else "0" + str
  }

  def extractDay(str: String) = {
    if(str.length <= 2 && str.forall(_.isDigit)) {
      val day = str.toInt
      if(day <= 31) Some(toTwoChars(str)) else None
    }else None
  }

  def extractYear(str:String):Option[String] = {
    if(str.length == 4 && str.forall(_.isDigit)) {
      val res = str.toInt
      if(res > 1980 && res < 2100) Some(str) else None
    } else {
      None
    }
  }

  def toDate(date: Seq[String]):Option[String] = {
    val year = extractYear(date(2))
    if(!year.isEmpty){
      val (day, month) = if(date(0).forall(_.isDigit))
        (extractDay(date(0)), extractMonth(date(1)))
      else if(!date(0).forall(_.isDigit) && date(1).forall(_.isDigit))
        (extractDay(date(1)), extractMonth(date(0)))
      else (None, None)

      if(!day.isEmpty && !month.isEmpty){
        Some(List(day, month, year).map(_.get).mkString("-"))
      }else None
    }else None
  }

}
