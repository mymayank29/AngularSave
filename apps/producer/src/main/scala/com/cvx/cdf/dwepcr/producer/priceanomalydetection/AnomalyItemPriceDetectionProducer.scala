package com.cvx.cdf.dwepcr.producer.priceanomalydetection

import com.cvx.cdf.dwepcr.producer.service.SparkConnector
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManagerDB.vwDwepPoAndSeLineItem
import org.apache.commons.math3.stat.descriptive.rank
import org.apache.spark.sql.expressions.Window

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import org.apache.commons.math3.stat.descriptive.moment.Mean
import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.functions._
import org.apache.commons.math3.stat.descriptive.rank.Percentile

object AnomalyItemPriceDetectionProducer {
  import SparkConnector.spark
  import spark.implicits._
  import com.cvx.cdf.dwepcr.producer.priceanomalydetection.Functions._

  def produceAnomalyItem = {
    val percentile10: Percentile = new Percentile(10)
    val standardDeviationCalc = new StandardDeviation()
    val meanCulc = new Mean()

    def unixTimeToDateTime(ut: org.apache.spark.sql.Column) = {
      to_utc_timestamp(ut.divide(1000).cast("timestamp"), "CST")
    }

    val lineItems = DataManager.get.vwDwepPoAndSeLineItemAnomalyDetector.
      filter(unixTimeToDateTime('source_approved_date) > "2016-10-01"
        && 'spend_type_name === "Non-Catalog"
        && 'quantity_llf > 0)
    val contracts = DataManager.get.vwDwepContracts

    val lineItemsContracts = lineItems.join(contracts, "contract_id").select(lineItems("*")).cache()

    def skewness(prices: Array[Double]): Option[Double] = {
      val n = prices.size
      val G_1 = if (n > 2 && standardDeviationCalc.evaluate(prices) != 0) {
        val mean = meanCulc.evaluate(prices)
        val x_centered = prices.map(x => x - mean)
        val g_1 = math.sqrt(n) * x_centered.map(x => math.pow(x, 3)).sum / math.pow(x_centered.map(x => math.pow(x, 2)).sum, 3 / 2)
        Some(g_1 * math.sqrt(n * (n - 1)) / (n - 2))
      } else {
        None
      }
      G_1
    }

    val skewnessUDF = udf((prices: Seq[String]) => 1)


    val percentile10UDF = udf((prices: Seq[Double]) => {
      percentile10.evaluate(prices.toArray)
    })

    val stdevUDF = udf((prices: Seq[Double]) => {
      standardDeviationCalc.evaluate(prices.toArray)
    })

    val lineItemWithDiffPrice = lineItemsContracts.
      withColumn("description", lower('description)).
      withColumn("unit_price_llf", 'unit_price_llf.cast("Double")).
      withColumn("quantity_llf", 'quantity_llf.cast("Double")).
      withColumn("expanded_costs", 'unit_price_llf * 'quantity_llf)

    //todo in [13,14,15] only for analizes
    //todo in [17] only for analizes 'price_distinct_count ,'uom_llf_distinct_count
    val result = lineItemWithDiffPrice.
      groupBy('contract_id, 'description.as("line_item_desc")).
      agg(
        collect_list('unit_price_llf).as("prices"),
        collect_list('expanded_costs).as("expends"),
        collect_set('commodity_id).as("commodity_id_set"),
        collect_set('mat_item_number).as("mat_item_number_set"),
        count('contract_id).as("num_mult_line_items"),
        countDistinct('unit_price_llf).as("Number_Unique"), //  "Number_Unique": curDescUnitPrice.nunique(),
        countDistinct('uom_llf).as("uom_llf_distinct_count"),
        sum('amount_after_discount_src).as("Sum_Cost_Total"),
        mean('unit_price_llf).as("Mean_Unit_Price"),
        min('unit_price_llf).as("Min_Unit_Price"),
        max('unit_price_llf).as("Max_Unit_Price"),
        count('expanded_costs).as("N")
      ).
      filter('num_mult_line_items > 0 && 'Number_Unique > 1).
      withColumn("Commondity_ID", concat_ws(",", 'commodity_id_set)). //  "Commondity ID": curDescDat.commodity_id.unique(),
      withColumn("Supplier_ID", size('commodity_id_set) > 1). //  "Supplier_ID": curDescDat.supplier_id.unique(),
      withColumn("Material_Item_Number", concat_ws(",", 'mat_item_number_set)). //  "Material_Item_Number": curDescDat.mat_item_number.unique(),
      withColumn("Line_Item_Desc_Length", size(split('line_item_desc, " "))). //  "Line_Item_Desc_Length": numWordsDesc,
      withColumn("Line_Item_Desc_MiscLabor", 'line_item_desc.like("%misc%") || 'line_item_desc.like("%labor%")). //  "Line_Item_Desc_MiscLabor": miscLabor,
      withColumn("curSkew", stdevUDF('prices)). //  "SD_Unit_Price": statistics.stdev(curDescUnitPrice),
      withColumn("Range_Unit_Price", 'Max_Unit_Price - 'Min_Unit_Price). //  "Range_Unit_Price": statRange(curDescUnitPrice),
      withColumn("Weighted_SD_Unit_Price", stdevUDF('expends)). //Why expandedCosts?//  "Weighted_SD_Unit_Price": statistics.stdev(expandedCosts),
      withColumn("curSkew", skewnessUDF('prices)). //  "Skew": curSkew,
      withColumn("Skew_Cumulative_Total", percentile10UDF('expends)).drop("prices","expends","").
      drop("commodity_id_set", "mat_item_number_set")//  "Skew_Cumulative_Total": skewCumul,

    result
  }

}
