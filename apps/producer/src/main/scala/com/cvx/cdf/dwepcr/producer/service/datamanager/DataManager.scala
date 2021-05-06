package com.cvx.cdf.dwepcr.producer.service.datamanager

import com.cvx.cdf.dwepcr.producer.service._
import org.apache.spark.sql.{DataFrame, SparkSession}

trait DataManager extends Serializable{
  def spark: SparkSession

  def wvJobIntervalProblem:DataFrame

  def wvJobRig:DataFrame

  def wvJobServiceContract:DataFrame

  def wvJobReportTimelog:DataFrame

  def wvVendorParentMapping:DataFrame

  def vwDwepPoAndSeLineItem:DataFrame

  def vwDwepPoAndSeLineItemAnomalyDetector:DataFrame

  def activityCodeMapping:DataFrame

  def wvJobReport:DataFrame

  def wvJobAfe:DataFrame

  def wvWellHeader:DataFrame

  def nptDetailCodeMapping:DataFrame

  def activityToContractsExceptionsMapping:DataFrame

  def wvJob:DataFrame

  def wvJobReportCostGen:DataFrame

  def vwDwepContracts:DataFrame

  def sapToWellview:DataFrame

  def pdfContractInfo:DataFrame

  def contractsWithAmendments:DataFrame

  def saveDataFrame(ds:DataFrame, tableName:String, location :String, dataBase :String): Unit
}

object  DataManager{
 def get:DataManager = Config.testMode match {
   case "test" => DataManagerTest
   case "local" => DataManagerLocal
   case _ => DataManagerDB
 }
}