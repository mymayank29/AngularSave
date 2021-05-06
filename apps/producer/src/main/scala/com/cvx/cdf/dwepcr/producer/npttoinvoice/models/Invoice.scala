package com.cvx.cdf.dwepcr.producer.npttoinvoice.models

case class Invoice(contract_title:String, contract_title_no_vendor:String, contract_id:String, submit_date:String, approved_date:String, inv_start_date:String,
                   inv_end_date:String, is_date_valid:Boolean,supplier_name_without_id:String, ariba_doc_id:String, invoice_number:String,
                   code:Seq[String], commodity_ids:String ,price:Double, supplier_id:String, wbs_element_id:String,
                   wbs_element:String, parent_vendor:String, description:String, contract_owner_name:String)

