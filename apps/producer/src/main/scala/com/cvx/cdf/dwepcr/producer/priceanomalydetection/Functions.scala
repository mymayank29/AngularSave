package com.cvx.cdf.dwepcr.producer.priceanomalydetection


import org.apache.commons.lang3.StringUtils
import org.apache.commons.math3.exception.MathIllegalArgumentException
import org.apache.commons.math3.linear.BlockRealMatrix
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.collection.{Map, Seq, Set, mutable}

object Functions extends Serializable {
  def calcDiv(prices: Array[Double]) = {
    prices.min / prices.max
  }

  import org.apache.commons.math3.stat.inference.OneWayAnova
//  @transient val oneWayAnova = new OneWayAnova()
  def findOneWayAnovaCorrelation(source: List[Array[Double]])= {
    val oneWayAnova = new OneWayAnova()
    try {Some(1 - oneWayAnova.anovaPValue(source.asJava))} catch  {case _: Throwable => None}
  }

  import org.apache.commons.math3.stat.correlation.SpearmansCorrelation
//  @transient val spearmansCorrelation = new SpearmansCorrelation()
  def findSpearmansCorrelation(f1:Array[Double], f2:Array[Double])= {
    val spearmansCorrelation = new SpearmansCorrelation()
    try {Some(spearmansCorrelation.correlation(f1, f2))} catch  {case _: MathIllegalArgumentException => None}
  }

  def calculateDeviation(prices: Seq[Double]) : Double = {
    val average = prices.sum / prices.size
    var variance : Double = 0
    for (price <- prices) {
      variance += math.pow(price - average, 2)
    }
    variance / prices.size
  }

  def findMode[A <: AnyVal](list: Seq[A]): A = {
    list.groupBy(rec => rec).map(rec => (rec._1, rec._2.size)).
      reduce((w1, w2) => if (w1._2 > w2._2) w1 else w2)._1
  }

  def takeMiddle(seq: Seq[Double], dropPercent:Double)= {
    val dropCount = (seq.size * dropPercent).toInt
    seq.drop(dropCount).dropRight(dropCount)
  }

  def getSeason(date: Int) = {
    date  % 10000 / 100 match {
      case n if Seq(12,1,2).contains(n) => "Winter"
      case n if Seq(3,4,5).contains(n) => "Spring"
      case n if Seq(6,7,8).contains(n) => "Summer"
      case _ => "Autumn"
    }
  }

  val oilPricemap = Map(
    201306 -> 99.74, 201307 -> 105.26, 201308 -> 108.16, 201309 -> 108.76, 201310 -> 105.43,
    201311 -> 102.63, 201312 -> 105.48, 201401 -> 102.10, 201402 -> 104.83, 201403 -> 104.04,
    201404 -> 104.87, 201405 -> 105.71, 201406 -> 108.37, 201407 -> 105.23, 201408 -> 100.05,
    201409 -> 95.85, 201410 -> 86.08, 201411 -> 76.99, 201412 -> 60.70, 201501 -> 47.11,
    201502 -> 57.49, 201503 -> 52.83, 201504 -> 57.54, 201505 -> 62.51, 201506 -> 61.31,
    201507 -> 54.34, 201508 -> 45.69, 201509 -> 46.28, 201510 -> 46.96, 201511 -> 43.11,
    201512 -> 36.57, 201601 -> 29.78, 201602 -> 31.03, 201603 -> 37.34, 201604 -> 40.75,
    201605 -> 45.94, 201606 -> 47.69, 201607 -> 44.13, 201608 -> 44.88, 201609 -> 45.04,
    201610 -> 49.29, 201611 -> 45.26, 201612 -> 52.62, 201701 -> 53.59, 201702 -> 54.35,
    201703 -> 50.90, 201704 -> 52.16, 201705 -> 49.89, 201706 -> 46.17, 201707 -> 47.66,
    201708 -> 49.94, 201709 -> 52.95, 201710 -> 54.92, 201711 -> 59.93, 201712 -> 61.19,
    201801 -> 66.23, 201802 -> 63.46, 201803 -> 64.17, 201804 -> 68.79, 201805 -> 73.43,
    201806 -> 79.23, 201807 -> 74.46
  )
  val getOilPriceUDF = udf((start_date: Int) => oilPricemap.getOrElse(start_date / 100, 0D))

  def strToForeGram(str: String) =
    (if (str == null) "" else str.toLowerCase.replaceAll("[^0-9a-z]", "")) match {
      case l => List(l.take(4), l.takeRight(4))
    }

  def strToForeGramUDF = udf((str: String) => strToForeGram(str))

  def isStringsSimilar(str1: String, str2: String) = {
    val th = Math.min(Math.min(str1.size, str2.size) / 4, 1)
    val levenshteinDistance = StringUtils.getLevenshteinDistance(str1, str2, 2)
    levenshteinDistance <= th && levenshteinDistance >= 0
  }

  def strToSet(str: String) = {
    str.toLowerCase.split("[^a-z0-9]+").toSet
  }



  def descSimilarity(descArray1: Seq[String], descArray2: Seq[String]) = {
    if(Math.max(descArray1.size, descArray2.size) > 500){
      0D
    } else {
      (for (
        desc1 <- descArray1.map(strToSet);
        desc2 <- descArray2.map(strToSet)) yield jaccardSimilarity(desc1, desc2)
        ).max
    }
  }

  def isSimilarityByPrice(price1Min: Int, price1Max: Int, price2Min: Int, price2Max: Int) = {
    def saveDivide(p1: Int, p2: Int) = {
      if (p1 == 0 && p2 == 0)
        0
      else if (p1 != 0 && p2 == 0) Integer.MAX_VALUE
      else p1 / p2
    }

    def getDifference(p1: Int, p2: Int) = Math.max(saveDivide(p1, p2), saveDivide(p2, p1))

    Seq(
      getDifference(price1Min, price2Min),
      getDifference(price1Min, price2Max),
      getDifference(price1Max, price2Min),
      getDifference(price1Max, price2Min)
    ).min
  }

  def median(list:Array[Double]): Double ={
    val sortedList = list.sorted
    val listSize = list.size
    if(listSize % 2 != 0)
      sortedList(listSize / 2)
    else (sortedList(listSize / 2 - 1) + sortedList(listSize / 2)) / 2
  }

  def unionAll(df1:DataFrame, df2:DataFrame) ={
    df1.union(df2.select(df1.columns.map(col):_*))
  }

  def parseDateFromInt(dateToParse: Int) : String = {
    val dateString = dateToParse.toString
    if(dateString.length == 8){
       dateString.substring(0,4) + "/" + dateString.substring(4,6) + "/" + dateString.substring(6,8)
    } else ""
  }

  def getMostRecentDate(startDate: Long, dates_from_title: Seq[Long], dates_from_text: Seq[Long]): Option[Long] = {
      val datesToCompare = if(dates_from_title != null && dates_from_title.nonEmpty) {
        dates_from_title
      } else if (dates_from_text != null && dates_from_text.nonEmpty) {
        dates_from_text
      } else Seq()

      datesToCompare
        .filter(stamp => stamp < startDate)
        .sortWith(_ > _)
        .headOption
  }

  def jaccardSimilarity[T](desc1: Set[T], desc2: Set[T]) = {
    val union = desc1 ++ desc2
    val intersect = desc1.intersect(desc2)
    intersect.size.toDouble / union.size
  }

  val descSimilarityUDF = udf((str1: Seq[String], str2: Seq[String]) => descSimilarity(str1, str2))
  val setSimilarityUDF = udf((str1: Seq[String], str2: Seq[String]) => jaccardSimilarity(str1.toSet, str2.toSet))
  val isSimilarityByPriceUDF = udf((price1Min: Int, price1Max: Int, price2Min: Int, price2Max: Int) =>
    isSimilarityByPrice(price1Min, price1Max, price2Min, price2Max))
  val isStringsSimilarUDF = udf((str1: String, str2: String) => isStringsSimilar(str1, str2))
  val parseDateFromIntUDF = udf((date: Int) => parseDateFromInt(date))
  val getMostRecentDateUDF_ = udf[Option[Long], Long, Seq[Long],Seq[Long]](getMostRecentDate)

}
