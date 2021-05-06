package com.cvx.cdf.dwepcr.producer.contract.analyzer

import scala.util.matching.Regex

object DateFromStringExtractor extends Serializable {
  val DD_MMM_Y = new Regex("\\(([0-9]{1,2}+)([a-z]{3,10}+)([0-9]{2,4}+)\\)","day","month_t","year")
  val MMM_DD_Y = new Regex("([a-z]{3,10}+).{0,3}?([0-9]{1,2}).{0,7}?([0-9]{2,4}+)","month_t","day","year")
  val DD_MM_Y = new Regex("([0-9]{1,2}+).{0,3}?([0-9]{1,2}+).{0,7}?([0-9]{2,4}+)","day","month","year")
  //    val DATA_PATTERM_3  = new Regex("([a-z]{3}).{0,3}?([0-9]{2,4})","day","month_t","year")

  val dateRegExpList: Seq[Regex] = List(
    DD_MMM_Y,
    MMM_DD_Y,
    DD_MM_Y)

  def intMonthFromWord(str:String):Int = {
    str match {
      case "jan" | "january" => 1
      case "feb" | "february" => 2
      case "mar" | "march" => 3
      case "apr" | "april" => 4
      case "may" => 5
      case "jun" | "june" => 6
      case "jul" | "july" => 7
      case "aug" | "august" => 8
      case "sep" | "september" => 9
      case "oct" | "october" => 10
      case "nov" | "november" => 11
      case "dec" | "december" => 12
      case _ => 0
    }
  }

  def isValidYear(year:Int):Boolean = {
    year < 2100 && year > 1900
  }

  def isValidDay(day:Int):Boolean = {
    day <= 31 && day > 0
  }

  def isValidMonth(month:Int):Boolean = {
    month <= 12 && month > 0
  }

  def parseYear(year:String):Int = {
    if(year.length == 2){
      if(year.toInt < 50)
        year.toInt + 2000
      else
        year.toInt + 1900
    }else{
      year.toInt
    }
  }

  def getDataFromMatch(m: scala.util.matching.Regex.Match)= {

    val day = m.group("day").toInt
    val month = if(m.groupNames.contains("month_t")) intMonthFromWord(m.group("month_t").toLowerCase) else m.group("month").toInt
    val year = parseYear(m.group("year"))

    if(isValidYear(year) && isValidMonth(month) && isValidDay(day))
      Some(m.start, year.toString + "/" + month.toString + "/" + day.toString)
    else
      None
  }

  def findAllDates(str: String) = {
    dateRegExpList
      .flatMap(_.findAllMatchIn(str))
      .map(getDataFromMatch)
      .filter(_.nonEmpty)
      .map(_.get)
  }

  def getFirstDate(str: String)= {
    val dates = findAllDates(str)
    if(dates == null || dates.isEmpty){
      null
    }else{
      dates.sortBy(_._1).head._2
    }
  }
}
