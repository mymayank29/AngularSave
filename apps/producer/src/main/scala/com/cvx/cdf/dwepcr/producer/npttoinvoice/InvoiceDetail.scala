package com.cvx.cdf.dwepcr.producer.npttoinvoice

import com.cvx.cdf.dwepcr.producer.service.SparkConnector
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager

object InvoiceDetail {
  import SparkConnector.spark
  import spark.implicits._

  val invoce = DataManager.get.vwDwepPoAndSeLineItem.
    select('ariba_doc_id, 'part_number, 'description, ('unit_price_llf * 'quantity_llf).as("net_price"), 'unit_price_llf, 'quantity_llf, 'discount, 'uom_llf)

  invoce.distinct()

}
