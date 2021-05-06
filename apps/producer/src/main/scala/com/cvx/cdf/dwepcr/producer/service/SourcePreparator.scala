package com.cvx.cdf.dwepcr.producer.service

import com.cvx.cdf.dwepcr.producer.service.Config._
import org.apache.spark.sql.{DataFrame}
import org.apache.spark.sql.functions._

object SourcePreparator {
  import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManagerDB._
  import SparkConnector.spark.implicits._

  final val saveMode = "overwrite"

  def saveToDisk(df:DataFrame, name:String, saveModeL:String = saveMode) = {
    df.repartition(1).write.mode(saveModeL).
      option("header", true).csv(s"${testSourcePath}/${name}")
    df
  }

  def regenerateSource()= {
    val idWellFilter = 'idwell.isin("C08A5A79F06C4B06898206B630820053")

    wvWellHeader.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvwellheaderName))
    wvJobServiceContract.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvJobServiceContractName))
    wvJobReportTimelog.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvJobReportTimelogName))

    wvJobIntervalProblem.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvJobIntervalProblemName))
    wvJobRig.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvJobRigName))

    wvJobReport.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvJobReportName))
    wvJobAfe.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvJobAfeName))
    wvJob.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvjobName))
    wvJobReportCostGen.repartition('idwell).filter(idWellFilter).transform(saveToDisk(_, wvJobReportCostGenName))

    val afes = wvJobAfe.filter(idWellFilter).select('idwell, regexp_replace('afenumber,"-","").as("wbs_element_id"))

    val vwDwepPoAndSeLineItemFiltered = vwDwepPoAndSeLineItem.join(afes, "wbs_element_id")
    vwDwepPoAndSeLineItemFiltered.repartition('idwell).transform(saveToDisk(_, vwDwepPoAndSeLineItemName))

//    val vwDwepPoAndSeLineItemFilteredDetailFiltered = vwDwepPoAndSeLineItemAnomalyDetector.join(afes, "wbs_element_id")
//    vwDwepPoAndSeLineItemAnomalyDetector.repartition('idwell).transform(saveToDisk(_, vwDwepPoAndSeLineItemName))

    vwDwepContracts.as("c").join(vwDwepPoAndSeLineItemFiltered.
      select('contract_id, 'idwell).distinct(), "contract_id").select($"c.*", 'idwell).repartition('idwell).
      transform(saveToDisk(_, vwDwepContractsName))

    // only initial run
    wvVendorParentMapping.transform(saveToDisk(_, wvVendorParentMappingName,"overwrite"))
    activityCodeMapping.transform(saveToDisk(_, activityCodeMappingName,"overwrite"))
    nptDetailCodeMapping.transform(saveToDisk(_, nptDetailCodeMappingName,"overwrite"))
    sapToWellview.transform(saveToDisk(_, sapToWellviewName,"overwrite"))
    activityToContractsExceptionsMapping.transform(saveToDisk(_, activityExceptionsMappingName,"overwrite"))

  }
}