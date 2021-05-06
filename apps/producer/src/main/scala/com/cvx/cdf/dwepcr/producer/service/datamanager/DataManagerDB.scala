package com.cvx.cdf.dwepcr.producer.service.datamanager

import com.cvx.cdf.dwepcr.producer.service.{CommonUDF, Config, SparkConnector}
import org.apache.spark.sql.DataFrame

object DataManagerDB extends DataManager{
  val spark = SparkConnector.spark
//  spark.sparkContext.setLogLevel("ERROR")
  import CommonUDF._
  import Config._
  import SparkConnector.spark.implicits._

  private def readTable(tableName:String) ={
    spark.table(Config.ciaStgDbName + "." + tableName)
  }

  private def readTableFromLake(tableName:String) ={
    spark.table(Config.ciaLakeDbName + "." + tableName)
  }

  override lazy val wvJobIntervalProblem:DataFrame = readTable(wvJobIntervalProblemName).
    select('idwell, 'idrecparent, 'idreclastrigcalc, 'idrec, 'com, 'costcalc, 'dttmend, 'dttmstart, 'durationgrosscalc, 'estcostoverride, 'idrecjobservicecontract, 'refno, 'status, 'typ, 'typdetail, 'idrecjobprogramphasecalc).
    withColumn("npt_date_start", unixTimeToDateTime('dttmstart)).
    withColumn("npt_date_end", unixTimeToDateTime('dttmend)).cache()

  override lazy val wvJobServiceContract = readTable(wvJobServiceContractName).
    select('idwell, 'idrecparent, 'idrec, 'servicecompany, 'servicecompanyparent, 'servicetyp).cache()

  override lazy val wvJobRig = readTable(wvJobRigName).
    select('idwell, 'idrec, 'rigno).cache()

  override lazy val wvJobReportTimelog = readTable(wvJobReportTimelogName).
    select( 'idwell, 'idrecparent, 'idrec, 'code1, 'code2, 'com, 'dttmendcalc, 'dttmstartcalc, 'duration, 'refnoproblemcalc).cache()

  override lazy val vwDwepPoAndSeLineItem = readTable(vwDwepPoAndSeLineItemName).
    select('ariba_doc_id, 'mat_item_number, 'commodity_id, 'business_key, 'source_approved_date, 'csid_parent_name, 'invoice_number, 'description,
      'account_id, 'spend_type_name, 'part_number, 'amount, 'amount_src, 'amount_after_discount_src, 'discount, 'quantity_llf, 'uom_llf, 'unit_price_llf,
      'load_update_time, 'end_date, 'start_date, 'contract_id, 'supplier_id, 'local_ref_number, 'supplier_part_number, 'supplier_name_without_id, 'wbs_element, 'wbs_element_id, 'submit_date, 'company_code_id, 'corp_high4).
    filter(!'ariba_doc_id.like("%-CM%") &&
      'start_date >= "20150000" && 'quantity_llf > 0).cache()

  override lazy val vwDwepPoAndSeLineItemAnomalyDetector: DataFrame = vwDwepPoAndSeLineItem.
    filter('spend_type_name.isin("Non-Catalog", "Zero-Priced Catalog"))

  override lazy val wvJobReport = readTable(wvJobReportName).
    select('idwell, 'idrecparent, 'idrec, 'dttmend, 'dttmstart, 'idrecjobprogramphasecalc).cache()

  override lazy val wvJobAfe = readTable(wvJobAfeName).
    select('idwell, 'idrecparent, 'idrec, 'afenumber, 'com, 'typ).cache()

  override lazy val wvWellHeader = readTable(wvwellheaderName).
    select('idwell, 'com, 'wellname).cache()

  override lazy val wvJob = readTable(wvjobName).
    select( 'idwell, 'idrec, 'dttmend, 'dttmstart, 'jobtyp, 'wvTyp).cache()

  override lazy val wvJobReportCostGen = readTable(wvJobReportCostGenName).
    select( 'idwell, 'idrecparent, 'idrec, 'code1, 'code2, 'cost, 'idrecafecustom, 'vendor).cache()

  override lazy val vwDwepContracts = readTable(vwDwepContractsName).cache()

  override lazy val pdfContractInfo = readTableFromLake(pdfContractInfoName)
    .cache()

  override lazy val contractsWithAmendments = readTableFromLake(amendmentsToContractsTableName)
    .select('contract_id, 'date_From_Title, 'date_From_Text)
    .cache()

  override lazy val nptDetailCodeMapping = loadNptDetailCodeMapping
  override lazy val activityCodeMapping = loadActivityCodeMapping
  override lazy val wvVendorParentMapping = loadWvVendorParentMapping
  override lazy val activityToContractsExceptionsMapping = loadActivityExceptionsMapping
  override lazy val sapToWellview = loadSapToWellview

  override def saveDataFrame(ds:DataFrame, tableName:String, location :String, dataBase :String): Unit ={
    ds.write.option("path", location+"/"+tableName).mode("overwrite").saveAsTable(dataBase +"."+tableName)
  }

  def loadNptDetailCodeMapping = spark.read.option("header", true).csv(nptDetailCodesMappingFilePath)
  def loadActivityCodeMapping = spark.read.option("header", true).csv(codeMappingFilePath)
  def loadWvVendorParentMapping = spark.read.option("header", true).csv(wvVendorParentMappingPath)
  def loadSapToWellview = spark.read.option("header", true).csv(sapToWellviewPath)
  def loadActivityExceptionsMapping = spark.read.option("header", true).csv(activityExceptionsPath)
}