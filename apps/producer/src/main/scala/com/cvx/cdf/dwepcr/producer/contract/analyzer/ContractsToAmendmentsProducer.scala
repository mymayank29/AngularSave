package com.cvx.cdf.dwepcr.producer.contract.analyzer

import com.cvx.cdf.dwepcr.producer.service.SparkConnector
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager
import org.apache.spark.sql.functions._

object ContractsToAmendmentsProducer {

  def produceAmendmentsToContractsTable()= {

    import SparkConnector.spark
    import spark.implicits._
    val vwContracts = DataManager.get.vwDwepContracts.as("contracts")
    val contractInfoDF = DataManager.get.pdfContractInfo
      .filter('is_amendment)
      .withColumn("amendment_contract_id", regexp_replace('contract_id, "[W]", ""))
      .as("amendments")

    contractInfoDF.join(vwContracts, $"contracts.contract_id" === $"amendments.amendment_contract_id")
      .drop(contractInfoDF("contract_id"))
  }
}
