package com.cvx.cdf.dwepcr.producer.npttoinvoice

import org.apache.spark.broadcast.Broadcast
import com.cvx.cdf.dwepcr.producer.service.Config._
import com.cvx.cdf.dwepcr.producer.npttoinvoice.models._
import org.apache.spark.sql.Dataset
import java.text.SimpleDateFormat
import java.util.Date
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.time.LocalTime



object NptJoinInvoicesFunctions extends Serializable{
  def safeMax(list: List[String]): String ={
    list.filter(_ != null) match {
      case Nil => ""
      case list => list.max
    }
  }

  def safeMin(list: List[String]): String ={
    list.filter(_ != null) match {
      case Nil => ""
      case list => list.min
    }
  }

def isIntersect(start1:String, end1:String, start2:String, end2:String):Boolean = {
    if(start1 == null || end1 == null || start2 == null || end2 == null) {
      return false
    }
    
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val invoiceStartDate = LocalDateTime.parse(start1, formatter).`with`(LocalTime.MIN)
    val invoiceEndDate = LocalDateTime.parse(end1, formatter).`with`(LocalTime.MAX)
    val nptStartDate = LocalDateTime.parse(start2, formatter)
    val nptEndDate = LocalDateTime.parse(end2, formatter)
  
    
    ((invoiceStartDate.isBefore(nptEndDate) || invoiceStartDate.isEqual(nptEndDate)) && (nptStartDate.isBefore(invoiceEndDate) || nptStartDate.isEqual(invoiceEndDate)));
  }

  // def isIntersect(start1:String, end1:String, start2:String, end2:String):Boolean = {
  //   start1 != null && end1 != null && ((start2.substring(0,10) <= end1.substring(0,10) && start2.substring(0,10) >= start1.substring(0,10))
  //     || (end2.substring(0,10) <= end1.substring(0,10) && end2.substring(0,10) >= start1.substring(0,10)))
  // }

  def calculateMatchByGLCode(invoice: Invoice, jobIntervalProblem: JobIntervalProblem, costDetail: CostDetail) : (String, Int) = {
    var resPercentage = 0
    val invoiceToCostgenCodeIntersection = invoice.code.intersect(if(costDetail == null || costDetail.code == null) Nil else costDetail.code)
    val invoiceToNPTCodeIntersection = invoice.code.intersect(
      if(jobIntervalProblem == null || jobIntervalProblem.gl_code == null) Nil else jobIntervalProblem.gl_code)
    val costGenToNPTCodeIntersection = invoiceToCostgenCodeIntersection.intersect(invoiceToNPTCodeIntersection)
    if(costGenToNPTCodeIntersection.nonEmpty && invoiceToNPTCodeIntersection.nonEmpty) {
      resPercentage = 100
    } else if(invoiceToNPTCodeIntersection.nonEmpty && invoiceToCostgenCodeIntersection.isEmpty) {
      resPercentage = 75
    } else if(invoiceToCostgenCodeIntersection.nonEmpty && invoiceToNPTCodeIntersection.isEmpty) {
      resPercentage = 50
    }
    val doesMatchByGlCode =  if(resPercentage > 0) "Yes" else "No"
    (doesMatchByGlCode, resPercentage)
  }

  def calculateMatchByTitleImproved(invoice: Invoice, jobIntervalProblem: JobIntervalProblem, nptToTitleSimilarity : Double,
                                    costDescToTitleSimilarity: Double,
                                    exceptionalMatchesDic: scala.collection.Map[String, Array[String]]) : (String, Int, Boolean) = {
    val contractTitle = invoice.contract_title
    val nptServicetype = jobIntervalProblem.servicetyp
    val costGenDescription = jobIntervalProblem.costgen_des
    val contract_id = invoice.contract_id
    val isExceptionalMatchCase = exceptionalMatchesDic.getOrElse(contract_id, Array()).contains(nptServicetype)

    val resPercentage = if(isExceptionalMatchCase)
      100
    else if(contractTitle == null || contractTitle.isEmpty)
      0
    else if((costGenDescription != null && costGenDescription.nonEmpty && costDescToTitleSimilarity >= costGenmatchTreshold) &&
      (nptToTitleSimilarity < nptMatchTreshold) )
      50
    else if((nptServicetype != null && nptServicetype.nonEmpty && nptToTitleSimilarity >= nptMatchTreshold) &&
      costDescToTitleSimilarity < costGenmatchTreshold)
      75
    else if(nptServicetype != null && nptServicetype.nonEmpty && costGenDescription != null && costGenDescription.nonEmpty &&
      nptToTitleSimilarity >= nptMatchTreshold && costDescToTitleSimilarity >= costGenmatchTreshold)
      100
    else 0

    val doesMatchByTitle = if(resPercentage > 0) "Yes" else "No"
    (doesMatchByTitle, resPercentage, isExceptionalMatchCase)
  }

  def calculateMatchByInvoiceDescription(jaccardSimilarity: Double): String = {
    if(jaccardSimilarity >= invoice_to_npt_descr_treshold) "Yes" else "No"
  }

  def sortByDates(dateOne: String, dateTwo: String, reverse: Boolean) = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val res = format.parse(dateOne).compareTo(format.parse(dateTwo))
    if(reverse) {
      format.parse(dateOne).compareTo(format.parse(dateTwo)) == 1
    } else {
      format.parse(dateOne).compareTo(format.parse(dateTwo)) != 1
    }
  }
  
  def calculateNptDurationWithinInvoice(invoiceStart:String, invoiceEnd:String, nptStart:String, nptEnd:String):Double = {
    if(invoiceStart == null || invoiceEnd == null || nptStart == null || nptEnd == null) {
      return 0D
    }
    
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val invoiceStartDate = LocalDateTime.parse(invoiceStart, formatter).`with`(LocalTime.MIN)
    val invoiceEndDate = LocalDateTime.parse(invoiceEnd, formatter).`with`(LocalTime.MAX)
    val nptStartDate = LocalDateTime.parse(nptStart, formatter)
    val nptEndDate = LocalDateTime.parse(nptEnd, formatter)
    
    var durationMillis = 0L
    
    if(nptEndDate.compareTo(invoiceStartDate) <= 0 || invoiceEndDate.compareTo(nptStartDate) <= 0) { //NPT is outside invoice
      return 0D
    } else if(nptStartDate.compareTo(invoiceStartDate) <= 0) { //NPT begins before invoice
      if(invoiceEndDate.compareTo(nptEndDate) <= 0) {
        durationMillis = Duration.between(invoiceStartDate, invoiceEndDate).toMillis()
      } else {
        durationMillis = Duration.between(invoiceStartDate, nptEndDate).toMillis()
      }
    } else if(invoiceEndDate.compareTo(nptEndDate) <= 0) { //Invoice ends before NPT
      durationMillis = Duration.between(nptStartDate, invoiceEndDate).toMillis()
    } else if(nptStartDate.compareTo(invoiceStartDate) >= 0 && nptEndDate.compareTo(invoiceEndDate) <= 0) { //NPT is fully within Invoice
      durationMillis = Duration.between(nptStartDate, nptEndDate).toMillis()
    }
    
    BigDecimal(durationMillis.toDouble / (1000*60*60)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def evaluate(invoiceNpt: (JobIntervalProblem, CostDetail, Invoice, Double, Double, Double),
               exceptionalMatchesDic: scala.collection.Map[String, Array[String]]): InvoiceToNptLink = {
    val (jobIntervalProblem, costDetail, invoice, nptToTitleSimilarity, costDescToTitleSimilarity, inv_descr_to_npt_similarity) = invoiceNpt
    if (invoice == null) {
      InvoiceToNptLink(
        ariba_doc_id = None,
        npt_sub_group_duration = 0,
        npt_sub_group_duration_bydate_only = 0,
//        cost_gen_id =   if(costDetail == null) null else costDetail.cost_gen_id,
        interval_problem_id = jobIntervalProblem.interval_problem_id,
        byCode = None,
        byDate = None,
        byTitle = None,
        byGlCode = "No",
        byGlCodePercents = 0,
        byTitleImpr = "No",
        byTitleImprPercents = 0,
        byInvoiceDescription = "No",
        isExceptionalMatchCase = false,
        isEmptyDate = None
      )
    } else {
      val matchByGlCode = calculateMatchByGLCode(invoice, jobIntervalProblem, costDetail)
      val matchByTitleCalculated = calculateMatchByTitleImproved(invoice, jobIntervalProblem, nptToTitleSimilarity, costDescToTitleSimilarity, exceptionalMatchesDic)
      val matchByInvoiceDescription = calculateMatchByInvoiceDescription(inv_descr_to_npt_similarity)
      InvoiceToNptLink(
        ariba_doc_id = Some(invoice.ariba_doc_id),
        npt_sub_group_duration = invoiceNpt._1.duration.getOrElse(0),
        npt_sub_group_duration_bydate_only = calculateNptDurationWithinInvoice(invoiceNpt._3.inv_start_date, invoiceNpt._3.inv_end_date, invoiceNpt._1.npt_date_start, invoiceNpt._1.npt_date_end),
//        cost_gen_id = if(costDetail == null) null else costDetail.cost_gen_id,
        interval_problem_id = jobIntervalProblem.interval_problem_id,
        byCode = Some(if(costDetail == null) false else invoice.code.intersect(costDetail.code).nonEmpty),
        byDate = Some(if(costDetail == null) false else invoice.is_date_valid && isIntersect(invoice.inv_start_date,invoice.inv_end_date,
          jobIntervalProblem.npt_date_start, jobIntervalProblem.npt_date_end)),
        byTitle = Some(nptToTitleSimilarity > 0),
        byGlCode = matchByGlCode._1,
        byGlCodePercents = matchByGlCode._2,
        byTitleImpr = matchByTitleCalculated._1,
        byTitleImprPercents = matchByTitleCalculated._2,
        byInvoiceDescription = matchByInvoiceDescription,
        isExceptionalMatchCase = matchByTitleCalculated._3,
        isEmptyDate = Some(invoice.is_date_valid)
      )
    }
  }



}
