package com.cvx.cdf.dwepcr.producer

import com.cvx.cdf.dwepcr.producer.service.SparkConnector
import org.apache.spark.sql.types.StructField
object StructureChangeChecker extends App{
  import SparkConnector.spark

  val dbNameSource = "cdf_lake_invoiceanalytics_sf"
  val dbNameDesd = "cdf_lake_invoiceanalytics_d"

  val tables = List("invoice_for_full_sf","interval_problem_full_sf","afe_detail","interval_problem_full_with_link_sf","time_log_detail")

  def equal2(rec: StructField, field: StructField): Boolean = {
    rec.name.toLowerCase == field.name.toLowerCase && rec.dataType == field.dataType
  }

  tables.foreach(rec => {
    val tbl1 = spark.table(s"${dbNameSource}.${rec}")
    val tbl2 = spark.table(s"${dbNameDesd}.${rec}")
    val diff = tbl1.schema.toList.filter(rec => !tbl2.schema.toList.exists(equal2(rec, _)))
    if(!diff.isEmpty){
      println()
      println(rec)
      println(diff)
    }
  })
}