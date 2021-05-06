package com.cvx.cdf.dwepcr.producer.service


import com.cvx.cdf.dwepcr.producer.npttoinvoice.NptJoinInvoices.invoice_view
import org.apache.spark.sql.SparkSession

object Config extends Serializable{
  val DBStgPostfix = "_d"
  val DBLakeUserPostfix = "pd"

  var ciaLakePath = s"/data/Prepared/svccdf$DBLakeUserPostfix/CDF_InvoiceAnalytics"

  final val CHEVRON_VENDOR_NAME = "CHEVRON"

  var stagePath = s"/data/Stage/svccdf${DBLakeUserPostfix}/CDF_InvoiceAnalytics"
  var appPath = s"/user/svccdf${DBLakeUserPostfix}/CDF_InvoiceAnalytics/app/producer"


  var ciaStgDbName: String = "cdf_stg_invoiceanalytics_d"
  var ciaLakeDbName: String = s"cdf_lake_invoiceanalytics$DBStgPostfix"

  val wvJobIntervalProblemName = "wvjobintervalproblem"
  val wvJobServiceContractName = "wvjobservicecontract"
  val wvJobReportTimelogName = "wvjobreporttimelog"
  val wvJobAfeName = "wvjobafe"
  val wvwellheaderName = "wvwellheader"
  val wvjobName = "wvjob"
  val wvJobRigName = "wvjobrig"
  val wvJobReportName = "wvjobreport"
  val wvJobReportCostGenName = "wvjobreportcostgen"

  val vwDwepPoAndSeLineItemName = "simsmart_vw_dwep_po_and_se_line_item"

  val vwDwepContractsName  = "simsmart_vw_dwep_contracts"

  val pdfContractInfoName = "pdf_contract_info"
  val amendmentsToContractsTableName = "amendments_to_contracts"
  val overchargeDetailTableName = "overcharge_detail"
  val ciaInvoiceName = "invoice"
  val ciaInvoiceDetailName = "invoice_detail"
  val ciaTimeLogDetailName = "time_log_detail"
  val ciaIntervalProblemDetailName = "interval_problem_detail"
  val ciaInvoiceToNptLinkDetailedName = "cia_invoice_to_npt_link_detailed"
  val ciaCostGetName = "cost_gen"
  val invoiceForFullSfName = "invoice_for_full_sf"
  val intervalProblemFullSfName = "interval_problem_full_sf"
  val intervalProblemFullWithLinkSfName = "interval_problem_full_with_link_sf"
  val intervalProblemDetailsName = "interval_problem_details"
  val ciaIntervalProblemName = "interval_problem"
  val ciaInvoiceToNptLinkName = "invoice_to_npt_link"
  val invoice_viewName = "invoice_for_sf"
  val afeDetailName = "afe_detail"
  val timeLogDetailName = "time_log_detail"
  val ciaPdfKeyValueName = "pdf_key_values"
  val simsmartVwDwepPoAndSeLineLtem = "simsmart_vw_dwep_po_and_se_line_item"

  val metadataTableName = "metadata_info"

  def testSourcePath = s"$appPath/testSource"

  // path to NPT detail codes mapping on HDFS :
  def codeMappingFilePath = s"$appPath/supplemental/mapping.csv"
  def nptDetailCodesMappingFilePath = s"$appPath/supplemental/unsched_events_npt_detail_codes.csv"
  def wvVendorParentMappingPath = s"$appPath/supplemental/wv_vendor_parent_mapping.csv"
  def sapToWellviewPath = s"$appPath/supplemental/sapToWellview.csv"
  def activityExceptionsPath = s"$appPath/supplemental/activityExceptions.csv"
  def amendmentsTrainDataPath = s"$appPath/resources/Amendments_TrainData.csv"

  val activityCodeMappingName = "activityCodeMapping"
  val nptDetailCodeMappingName = "nptDetailCodeMapping"
  val wvVendorParentMappingName = "wv_vendor_parent_mapping"
  val sapToWellviewName = "sap_to_wellview"
  val activityExceptionsMappingName = "activity_exceptions"

  var testMode = "db"

  // data science models paths
  val modelsPath = s"$appPath/models"

  // OCR properties
  def tessDataTrainingPath = s"$appPath/TESSERACT_INIT_DATA"
  var contractsSourcePath = "/data/cdf_drop/EDAP_GOMICA/Inbound/DEV/"
  def ocrResultPath = s"${stagePath}/ocr_res/"

  // Text similarity trasholds
  val nptMatchTreshold = 0.1
  val costGenmatchTreshold = 0.1
  val invoice_to_npt_descr_treshold = 0.1

  def readConfig(args: Array[String]) = {
    val paramMap = args.map(param => param.split("=",2)).map { case Array(kay, value) => (kay, value) }.toMap
  }
}
