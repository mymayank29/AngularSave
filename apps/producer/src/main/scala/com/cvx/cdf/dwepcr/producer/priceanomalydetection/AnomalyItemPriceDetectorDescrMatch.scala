package com.cvx.cdf.dwepcr.producer.priceanomalydetection

import com.cvx.cdf.dwepcr.producer.service.{Config, Logging, SparkConnector}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._



object AnomalyItemPriceDetectorDescrMatch extends Serializable with Logging {
  import SparkConnector.spark
  import spark.implicits._
  import AnomalyItemPriceDetectorPartNumber._
  import HierarchicalClusterizer.calculateClusters


  def calculatePotentialOvercharge: DataFrame = {

    def calculateSubGroupsUsingDescription(items: Array[LineItem]): Array[Array[LineItem]] = {

      if (items.length > 2000) return Array(items)

      val itemsWithDescriptions = items
        .map(item => (item, item.description.toLowerCase.split("\\W+").toSet))
      val vertecies = itemsWithDescriptions.map(_._2).toSet.toSeq
      val similarityTreshold = 0.55
      val accountIdToGSubgroup = calculateClusters(vertecies, similarityTreshold)

      val arrayOfClusters = itemsWithDescriptions
        .map(item => (accountIdToGSubgroup.getOrElse(item._2, -1), item._1))
        .groupBy(_._1)
        .map(r => r._2.map(_._2))
        .toArray

      arrayOfClusters
    }

    val grouped = AnomalyItemPriceDetectorPartNumber.calculate
    val groupedByDesc = grouped
      .filter(g => g._1.dispersion > 0 && g._1.have_only_non_catalog)
      .filter(_._1.deviation > 0)
      .map(rec => (rec._1, calculateSubGroupsUsingDescription(rec._2)
        .map(subGroup => (evaluateGroup(rec._1.part_number, rec._1.supplier_id ,subGroup), subGroup))
        )
      ).cache()

    groupedByDesc
//      .filter(g => g._1.dispersion > 0 && g._1.have_only_non_catalog && g._1.group_overcharge > MIN_OVERCHARGE)
      .toDF("group","dsc_group")
      .withColumn("groups", explode('dsc_group))
      .withColumn("group_id", monotonically_increasing_id())
      .withColumn("line_item", explode($"groups._2"))
      .withColumn("group", $"groups._1").drop("dsc_group", "groups")
      .select(
        'group_id.as("super_group_id"),
        $"group.part_number",
        when(isnan($"group.oil_and_gas_correlation"),lit(0)).otherwise($"group.oil_and_gas_correlation").as("oil_and_gas_correlation"),
        when(isnan($"group.seasonality_correlation"),lit(0)).otherwise($"group.seasonality_correlation").as("seasonality_correlation"),
        $"line_item.oilPrice",
        'group_id,
        $"line_item.wellname",
        $"line_item.wbs_element_id",
        $"line_item.contract_id",
        $"line_item.contract_owner_name",
        $"line_item.ariba_doc_id",
        $"line_item.uom_llf",
        $"line_item.quantity_llf",
        $"line_item.unit_price_llf",
        $"line_item.description",
        $"line_item.start_date",
        $"line_item.invoice_number",
        $"line_item.spend_type_name",
        ($"line_item.start_date" / 10000).cast("Int").as("Year"),
        $"group.supplier_name",
        $"group.group_overcharge".as("g_overcharge"),
        $"group.average".as("avg_price"),
        $"group.rate_of_change_benchmark",
        $"group.rate_of_change_rolling",
        $"group.exceeds_benchmark",
        lit(true).as("is_main_group"),
        lit("description_match").as("report_type"),
        calculateItemPotentialOverchargeUDF($"line_item.spend_type_name",$"line_item.start_date",
          $"line_item.unit_price_llf", $"line_item.quantity_llf", $"group.average").as("overcharge")
      )
      //.filter('overcharge > MIN_OVERCHARGE)
  }


//  def calculateClustersInGraph(graph: Map[Set[String], scala.collection.mutable.Set[Set[String]]]): Map[Set[String], Int] = {
//
//    val vertexClusterIds = mutable.Map[Set[String], Int]()
//    val visited = mutable.Set[Set[String]]()
//
//    def dfs(startVertex: Set[String], clusterID: Int): Unit = {
//      visited.add(startVertex)
//      vertexClusterIds.put(startVertex, clusterID)
//      val neighbours = graph.get(startVertex).get
//      for (vertex <- neighbours) {
//        if (!visited.contains(vertex)) {
//          dfs(vertex, clusterID)
//        }
//      }
//    }
//
//    var clusterID_ = 1
//    for (vertex <- graph.keys) {
//      if (!visited.contains(vertex)) {
//        dfs(vertex, clusterID_)
//        clusterID_ += 1
//      }
//    }
//    vertexClusterIds
//  }
//
//  def createSemiGraph(vertecies: Seq[Set[String]]): ListBuffer[(Set[String], Set[String])] = {
//    val similarityThreshold = 0.5
//    val semiGraph = new ListBuffer[(Set[String], Set[String])]
//    for (i <- vertecies.indices) {
//      for (j <- i + 1 until vertecies.length) {
//        val id_1 = vertecies(i)
//        val id_2 = vertecies(j)
//        val similarity = jaccardSimilarity(id_1, id_2)
//        if(similarity >= similarityThreshold){
//          semiGraph += ((id_1, id_2))
//        }
//      }
//    }
//    semiGraph
//  }
//
//  def createGraph(semiGraph: ListBuffer[(Set[String], Set[String], Double)]): mutable.Map[Set[String], ListBuffer[(Set[String], Double)]] = {
//    val graph = mutable.Map[Set[String], ListBuffer[(Set[String], Double)]]()
//    for (edge <- semiGraph) {
//      val firstNodeNeighbours = graph.getOrElse(edge._1, new ListBuffer())
//      firstNodeNeighbours += ((edge._2, edge._3))
//      graph.put(edge._1, firstNodeNeighbours)
//      val secondNodeNeighbours = graph.getOrElse(edge._2, new ListBuffer())
//      secondNodeNeighbours += ((edge._1, edge._3))
//      graph.put(edge._2, secondNodeNeighbours)
//    }
//    graph
//  }
//
//  def createGraph(vertecies: Seq[Set[String]]) : mutable.Map[Set[String], scala.collection.mutable.Set[Set[String]]] = {
//    val semiGraph = createSemiGraph(vertecies)
//    val graph = mutable.Map[Set[String], scala.collection.mutable.Set[Set[String]]]()
//    for (edge <- semiGraph) {
//      var firstNodeNeighbours = graph.getOrElse(edge._1,   scala.collection.mutable.Set[Set[String]]())
//      firstNodeNeighbours += edge._2
//      graph.put(edge._1, firstNodeNeighbours)
//      val secondNodeNeighbours = graph.getOrElse(edge._2,  scala.collection.mutable.Set[Set[String]]())
//      secondNodeNeighbours += edge._1
//      graph.put(edge._2, secondNodeNeighbours)
//    }
//    graph
//  }
}
