package com.cvx.cdf.dwepcr.producer.npttoinvoice.preprocessing

import com.cvx.cdf.dwepcr.producer.get_first
import com.cvx.cdf.dwepcr.producer.npttoinvoice.models.Invoice
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager
import org.apache.spark.sql.Dataset
import com.cvx.cdf.dwepcr.producer.service.{CommonUDF, SparkConnector}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object InvoicePreprocessor extends Serializable{
  import SparkConnector.spark
  import spark.implicits._
  import CommonUDF._

  def invoices:Dataset[Invoice] = {
    val MAX_INVOICE_DAY_NUMBER = 120
    val CORRECT_DATE_CONDITION =  ('inv_start_date < 'submit_date) &&
      ('contract_start_date.isNull || ('inv_start_date >= 'contract_start_date)) &&
      (datediff('inv_end_date, 'inv_start_date) <= MAX_INVOICE_DAY_NUMBER)

    val removeVendor = (title:String, vendorName:String) => {
      val titleWords = title.toLowerCase().split("\\W+|\\_").toList
      val vendorNameWords = vendorName.toLowerCase().split("\\W+|\\_").toList
      var titleWithoutVendor = titleWords

      var i = 0
      while (i < titleWithoutVendor.length) {
        if(titleWithoutVendor(i) == vendorNameWords.head) {
          var j = 0
          while (j < math.min(titleWithoutVendor.length-i, vendorNameWords.length) && titleWithoutVendor(i + j) == vendorNameWords(j)) {
            j += 1
          }
          titleWithoutVendor = titleWithoutVendor.take(i) ::: titleWithoutVendor.slice(i + j, titleWithoutVendor.length)
        }
        i += 1
      }
      titleWithoutVendor.mkString(" ")
    }
    
    val removeGenericWordsFromTitle = (title : String) => {

      val genericWordsToRemove = Set("international", "incorporated", "corporation", "corp", "companies", "service", "services",
        "technology", "inc", "llc", "l l c", "lp")
      
      var titleWords = title.toLowerCase().split("\\W+").toList
      for(word <- titleWords) {
        if(genericWordsToRemove.contains(word)) {
          val index = titleWords.indexOf(word)
          titleWords = titleWords.take(index) ::: titleWords.slice(index+1, titleWords.length)
        }
      }
      titleWords.mkString(" ")
    }

    def cleanContractTitle = (title:String, vendorName:String, parentVendorName:String) => {
      val titleWithoutVendor = removeVendor(title, vendorName)
      val titleWithoutParentVendor = removeVendor(titleWithoutVendor, parentVendorName)
      val titleWithoutGenericWords = removeGenericWordsFromTitle(titleWithoutParentVendor)
      titleWithoutGenericWords
    }

    val cleanContractTitleUDF = udf(cleanContractTitle)

    val invoicesPrepared = DataManager.get.vwDwepPoAndSeLineItem.select(
      unixTimeToDateTime('load_update_time).as("load_update_time"),
      from_unixtime(unix_timestamp('start_date.cast("string"), "yyyyMMdd")) as 'inv_start_date,
      from_unixtime(unix_timestamp('end_date.cast("string"), "yyyyMMdd")) as 'inv_end_date,
      from_unixtime(unix_timestamp('submit_date.cast("string"), "yyyyMMdd")).as("submit_date"),
      unixTimeToDateTime('source_approved_date) as 'approved_date,
      trim(get_first(split('supplier_name_without_id, ":"))).as("supplier_name_without_id"),
      'amount_after_discount_src.cast("Double").as("price"),
      'quantity_llf.cast("Double").as("quantity_llf"),
      'uom_llf, 'ariba_doc_id, 'invoice_number, 'description, 'account_id,
      'commodity_id, 'supplier_name_without_id.as("supplier_name_without"),
      coalesce('csid_parent_name, lit("")).as("parent_vendor"),
      'unit_price_llf, 'supplier_id, trim('wbs_element).as("wbs_element"),
      trim('wbs_element_id).as("wbs_element_id"), 'contract_id, 'description, 'business_key
    ).join(DataManager.get.sapToWellview, 'account_id === concat(lit("00"),'sap_cost_element)).cache()

    val contracts = DataManager.get.vwDwepContracts.select('contract_id, 'contract_title, 'contract_owner_name,
      unixTimeToStringDate('contract_effective_date).as("contract_start_date"),
      unixTimeToStringDate('contract_expiration_date).as("contract_end_date")
    ).distinct()

   invoicesPrepared.join(contracts, Seq("contract_id"), "left").
      withColumn("is_date_valid", CORRECT_DATE_CONDITION || CORRECT_DATE_CONDITION.isNull).
      groupBy('contract_owner_name, 'is_date_valid, 'contract_title, 'contract_id, 'inv_start_date, 'inv_end_date, 'submit_date, 'approved_date , 'supplier_name_without_id, 'ariba_doc_id, 'invoice_number
      , 'supplier_id, 'wbs_element_id, 'wbs_element, 'supplier_name_without, 'parent_vendor).
     agg(
       sum('price).as("price"),
       collect_set('wv_sap_new).as("code"),
       concat_ws(";", collect_set('commodity_id)).as("commodity_ids"),
       concat_ws(";", collect_set('description)).as("description")).
     withColumn("price", sum('price).over(Window.partitionBy('ariba_doc_id))).
     withColumn("contract_title_no_vendor", cleanContractTitleUDF(coalesce('contract_title, lit("")).as("contract_title"), 'supplier_name_without_id, 'parent_vendor)).
     withColumn("contract_id", regexp_replace('contract_id, lit("^C"), lit("CW"))).
     as[Invoice]
  }
}
