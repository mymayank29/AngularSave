package com.cvx.cdf.dwepcr.producer.priceanomalydetection

import com.cvx.cdf.dwepcr.producer.price.AnomalyItemPriceProducer
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

    val anomalyItemPriceDetector = AnomalyItemPriceDetectionProducer.produceAnomalyItem
    DataManager.get.saveDataFrame(anomalyItemPriceDetector, Config.overchargeDetailTableName, location, dataBase)
  }
}