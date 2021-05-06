package com.cvx.cdf.dwepcr.producer.priceanomalydetection

import com.cvx.cdf.dwepcr.producer.priceanomalydetection.AnomalyItemPriceDetectorPartNumber
import com.cvx.cdf.dwepcr.producer.service.{SparkConnector}

import org.apache.spark.sql.{DataFrame}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import scala.collection._

object AnomalyItemPriceDetectorPartialMatch extends Serializable {
  import SparkConnector.spark
  import AnomalyItemPriceDetectorPartNumber._
  import spark.implicits._
  import Functions._

  def calculatePotentialOvercharge: DataFrame = {
    val grouped = calculate
    val groupedWithGrams = grouped.withColumn("li", explode('_2)).select($"_1".as("group"),
      $"li.description",$"li.account_id",$"li.company_code_id",$"li.corp_high4").
      groupBy('group).agg(collect_set('description).as("description"), collect_set('account_id).as("account_id"),
      collect_set('company_code_id).as("company_code_id"), collect_set('corp_high4).as("corp_high4")).
      withColumn("part_number_gram", explode(strToForeGramUDF($"group.part_number")))

    val joined = groupedWithGrams.as("t1").join(groupedWithGrams.as("t2"), "part_number_gram").
      filter(($"t1.group.have_only_non_catalog" || $"t2.group.have_only_non_catalog")
        && !($"t1.group.part_number" === $"t2.group.part_number" && $"t1.group.supplier_id" === $"t2.group.supplier_id")
        && isStringsSimilarUDF($"t1.group.part_number", $"t2.group.part_number")
      )

    val groupedSelfJoin = joined.
      drop("part_number_gram").distinct().cache().
      withColumn("id_1", $"t1.group.id").
      withColumn("id_2", $"t2.group.id").
      withColumn("part_number_1", $"t1.group.part_number").
      withColumn("part_number_2", $"t2.group.part_number").
      withColumn("supplier_id_1", $"t1.group.supplier_id").
      withColumn("supplier_id_2", $"t2.group.supplier_id").
      withColumn("descriptions_similarity", descSimilarityUDF($"t1.description", $"t2.description")).
      withColumn("account_id_similarity", setSimilarityUDF($"t1.account_id", $"t2.account_id")).
      withColumn("company_code_id_similarity", setSimilarityUDF($"t1.company_code_id", $"t2.company_code_id")).
      withColumn("corp_high4_similarity", setSimilarityUDF($"t1.corp_high4", $"t2.corp_high4")).
      withColumn("isSimilarityByPrice", isSimilarityByPriceUDF($"t1.group.min_Price", $"t1.group.max_Price", $"t2.group.min_Price", $"t2.group.max_Price")).
      withColumn("amount", ($"t1.group.group_size" + $"t2.group.group_size").as("group_size")).
      withColumn("haveNonCatalog", ($"t1.group.have_non_catalog" || $"t2.group.have_non_catalog")).
      withColumn("same_supplier_id",($"t1.group.supplier_id" === $"t2.group.supplier_id")).
      filter('descriptions_similarity > 0.3 && 'account_id_similarity > 0 && 'isSimilarityByPrice <= 4 && 'same_supplier_id).cache()

    val finalRes =
      groupedSelfJoin.join(lineItem, 'part_number_1 === 'part_number && 'supplier_id_1 === 'supplier_id).
        union(
          groupedSelfJoin.join(lineItem, 'part_number_2 === 'part_number && 'supplier_id_2 === 'supplier_id)
        )

    val graphCached = finalRes.
      select('id_1.as("super_group_id"), explode(array('id_1, 'id_2)).as("id")).
      distinct().cache()

    val supergroupMapping = grouped.map(_._1).toDF().drop('line_items).
      join(graphCached.toDF("id", "super_group_id"), "id").cache()

    val sgWindow = Window.partitionBy('super_group_id)
    val groupWindow = Window.partitionBy('super_group_id, 'id)
    val lineItemWithSg = supergroupMapping.withColumn("sg_min_avg", min('average).over(sgWindow)).
      withColumn("is_main_group", 'super_group_id === 'id).
      withColumn("sg_haveNonCatalog", max('have_non_catalog && 'is_main_group).over(sgWindow)).
      filter('sg_haveNonCatalog).drop('uom_llf).
      withColumn("oil_and_gas_correlation", when(isnan($"oil_and_gas_correlation"),lit(0)).otherwise($"oil_and_gas_correlation")).
      withColumn("seasonality_correlation", when(isnan($"seasonality_correlation"),lit(0)).otherwise($"seasonality_correlation")).
      join(lineItem.drop("id", "supplier_name"), Seq("part_number", "supplier_id"), "left").
      drop("descriptions", "account_id", "company_code_id", "corp_high4", "local_ref_number", "supplier_part_number").
      withColumn("overcharge",
        when('have_only_non_catalog && 'super_group_id === 'id && 'start_date > START_DATE, greatest('unit_price_llf - 'sg_min_avg, lit(0)) * 'quantity_llf).otherwise(lit(null))).
      withColumn("g_overcharge", sum('overcharge).over(groupWindow)).
      withColumn("sg_overcharge", sum('overcharge).over(sgWindow)).
      withColumn("sgAmount", count('have_non_catalog).over(sgWindow)).
      cache()

    lineItemWithSg
      .filter('sg_overcharge > MIN_OVERCHARGE)
      .drop("sg_overcharge")
      .select('ariba_doc_id, 'super_group_id, 'wellname, 'wbs_element_id, 'contract_id, 'contract_owner_name, ('start_date / 10000).cast("Int").as("Year"),
        'part_number, 'id.as("group_id"), 'uom_llf, 'oilPrice, 'quantity_llf, 'unit_price_llf, 'spend_type_name, 'description, 'supplier_name,
        'start_date, 'invoice_number, 'overcharge, 'g_overcharge, 'average.as("avg_price"), 'oil_and_gas_correlation, 'seasonality_correlation,
        'rate_of_change_benchmark, 'rate_of_change_rolling,  'exceeds_benchmark, 'is_main_group, lit("partial").as("report_type")).cache
  }
}
