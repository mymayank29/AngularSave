package com.cvx.cdf.dwepcr.producer.npttoinvoice.models


case class  JobIntervalProblem(var afenumber:String, interval_problem_id:Long, servicetyp:String, rigno:String, idwell:String,
                              idrecntervalProblem:String,job_id:String,npt_date_start:String, npt_date_end:String,
                              parent_name:String, cost:Double, estcostoverride:Double, status:String, refno:String, group_refno:String,duration:Option[Double],
                              typ:String, typdetail:String, com:String, internalProblemType:String, gl_code:Array[String],
                              gl_code_str:String, costgen_des:String,activitydescr:String, npt_type_detail_description:String,
                               npt_type_typedetail_concat:String)
