package com.cvx.cdf.dwepcr.producer.npttoinvoice.models


case class CostDetail(afenumber:String, job_id:String,
                      parent_name:String, nptstart_date:String, code:Seq[String])



// case class CostDetail(cost_gen_id:String, cost_idwell:String, cost_idrec:String, 
//                       cost_refno:String, afenumber:String, job_id:String, code:Seq[String],
//                       parent_name:String, costdetail_cost:String, cost_jobtyp:String, 
//                       cost_servicetyp:String, nptstart_date:String, nptend_date:String)


