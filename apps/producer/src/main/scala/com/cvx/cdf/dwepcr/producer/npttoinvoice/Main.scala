package com.cvx.cdf.dwepcr.producer.npttoinvoice

import com.cvx.cdf.dwepcr.producer.npttoinvoice.NptJoinInvoices._
import com.cvx.cdf.dwepcr.producer.service.Config._
import com.cvx.cdf.dwepcr.producer.service.{Config, _}
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager

object Main extends Serializable with Logging {
  def main(args: Array[String]): Unit = {
    val Array(testMode, location, dataBase, stgLocation, stgDatabase, appPath) = args
    Config.testMode = testMode
    Config.stagePath = stgLocation
    Config.ciaStgDbName = stgDatabase
    Config.appPath = appPath
    Config.ciaLakeDbName = dataBase

    logger.info(s"[Spark] Main configs: ")
    logger.info("     testMode" + " = " + testMode)
    logger.info("     location" + " = " + location)
    logger.info("     dataBase" + " = " + dataBase)
    logger.info("     stgLocation" + " = " + stgLocation)
    logger.info("     stgDatabase" + " = " + stgDatabase)
    logger.info("     appPath" + " = " + appPath)


    SourcePreparator.regenerateSource()
    DataManager.get.saveDataFrame(ciaInvoiceToNptLinkDetailedView, ciaInvoiceToNptLinkDetailedName, location, dataBase)
    DataManager.get.saveDataFrame(ciaInvoiceDFView, ciaInvoiceName, location, dataBase)
    DataManager.get.saveDataFrame(ciaIntervalProblemDFView, ciaIntervalProblemName, location, dataBase)
    DataManager.get.saveDataFrame(ciaInvoiceToNptLinkDFView, ciaInvoiceToNptLinkName, location, dataBase)
    DataManager.get.saveDataFrame(invoiceForFullSfView, invoiceForFullSfName, location, dataBase)
    DataManager.get.saveDataFrame(intervalProblemFullSfView, intervalProblemFullSfName, location, dataBase)
    DataManager.get.saveDataFrame(afeDetailView, afeDetailName, location, dataBase)
    DataManager.get.saveDataFrame(intervalProblemFullSfWithLinkView, intervalProblemFullWithLinkSfName, location, dataBase)
    DataManager.get.saveDataFrame(timeLogDetailView, timeLogDetailName, location, dataBase)
    DataManager.get.saveDataFrame(intervalProblemDetailsView, intervalProblemDetailsName, location, dataBase)
  }
}