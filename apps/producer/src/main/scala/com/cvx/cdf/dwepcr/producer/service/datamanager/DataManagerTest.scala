package com.cvx.cdf.dwepcr.producer.service.datamanager

import com.cvx.cdf.dwepcr.producer.service.{Config, SparkConnector}
import org.apache.spark.sql.DataFrame

object DataManagerTest extends DataManager{
  val spark = SparkConnector.spark
  import Config._
  import SparkConnector.spark.implicits._

  private def readTableFromLake(tableName:String)= {
    spark.table(Config.ciaLakeDbName + ".test_" + tableName)
  }

  override lazy val wvJobIntervalProblem = spark.read.option("header", true).csv(s"$testSourcePath/$wvJobIntervalProblemName")
  override lazy val wvJobRig = spark.read.option("header", true).csv(s"$testSourcePath/$wvJobRigName")
  override lazy val wvJobServiceContract = spark.read.option("header", true).csv(s"$testSourcePath/$wvJobServiceContractName")
  override lazy val wvJobReportTimelog = spark.read.option("header", true).csv(s"$testSourcePath/$wvJobReportTimelogName")
  override lazy val wvVendorParentMapping = spark.read.option("header", true).csv(s"$testSourcePath/$wvVendorParentMappingName")
  override lazy val vwDwepPoAndSeLineItem = spark.read.option("header", true).csv(s"$testSourcePath/$vwDwepPoAndSeLineItemName")
  override lazy val vwDwepPoAndSeLineItemAnomalyDetector = spark.read.option("header", true).csv(s"$testSourcePath/$vwDwepPoAndSeLineItemName")
  override lazy val activityCodeMapping = spark.read.option("header", true).csv(s"$testSourcePath/$activityCodeMappingName")
  override lazy val wvJobReport = spark.read.option("header", true).csv(s"$testSourcePath/$wvJobReportName")
  override lazy val wvJobAfe = spark.read.option("header", true).csv(s"$testSourcePath/$wvJobAfeName")
  override lazy val wvWellHeader = spark.read.option("header", true).csv(s"$testSourcePath/$wvwellheaderName")
  override lazy val wvJob = spark.read.option("header", true).csv(s"$testSourcePath/$wvjobName")
  override lazy val wvJobReportCostGen = spark.read.option("header", true).csv(s"$testSourcePath/$wvJobReportCostGenName")
  override lazy val vwDwepContracts = spark.read.option("header", true).csv(s"$testSourcePath/$vwDwepContractsName")
  override lazy val sapToWellview = spark.read.option("header", true).csv(s"$testSourcePath/$sapToWellviewName")
  override lazy val nptDetailCodeMapping = spark.read.option("header", true).csv(s"$testSourcePath/$nptDetailCodeMappingName")
  override lazy val activityToContractsExceptionsMapping = spark.read.option("header", true).csv(s"$testSourcePath/$activityExceptionsMappingName")
  override lazy val pdfContractInfo = readTableFromLake(pdfContractInfoName).
    select('contract_id, 'path, 'is_amendment, 'date_From_Title, 'date_From_Text, 'non_Amendment_Words).cache()
  override lazy val contractsWithAmendments = readTableFromLake(amendmentsToContractsTableName)


  override def saveDataFrame(ds:DataFrame, tableName:String, location :String, dataBase :String): Unit ={
    ds.write.option("path", location+"/test_"+tableName).mode("overwrite").saveAsTable(dataBase +".test_"+tableName)
  }
}
