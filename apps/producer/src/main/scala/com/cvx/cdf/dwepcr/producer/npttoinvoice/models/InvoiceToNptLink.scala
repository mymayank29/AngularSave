package com.cvx.cdf.dwepcr.producer.npttoinvoice.models

case class InvoiceToNptLink(ariba_doc_id:Option[String], npt_sub_group_duration: Double, npt_sub_group_duration_bydate_only: Double,
                            interval_problem_id:Long, byDate: Option[Boolean], byCode: Option[Boolean],
                            byTitle: Option[Boolean], byGlCode: String, byGlCodePercents: Int, byTitleImpr: String, byTitleImprPercents: Int,
                            byInvoiceDescription: String,  isExceptionalMatchCase: Boolean, isEmptyDate:Option[Boolean])
                            
//
//case class InvoiceToNptLink(ariba_doc_id:Option[String], npt_sub_group_duration: Double, npt_sub_group_duration_bydate_only: Double,
//                            cost_gen_id:String, interval_problem_id:Long, byCode: Option[Boolean], byDate: Option[Boolean],
//                            byTitle: Option[Boolean], byGlCode: String, byGlCodePercents: Int, byTitleImpr: String, byTitleImprPercents: Int,
//                            byInvoiceDescription: String,  isExceptionalMatchCase: Boolean, isEmptyDate:Option[Boolean])
