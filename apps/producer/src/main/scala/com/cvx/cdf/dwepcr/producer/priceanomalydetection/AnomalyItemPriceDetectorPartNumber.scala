package com.cvx.cdf.dwepcr.producer.priceanomalydetection

import com.cvx.cdf.dwepcr.producer.service.CommonUDF.unixTimeToDateTime
import com.cvx.cdf.dwepcr.producer.service.SparkConnector
import com.cvx.cdf.dwepcr.producer.service.datamanager.DataManager
import org.apache.commons.math3.stat.descriptive.rank.Percentile
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

case class LineItem(id:Long, invoice_number: String, wellname:String,wbs_element_id:String,contract_id:String,contract_owner_name:String,
                    ariba_doc_id: String, amount:Double, business_key: String, account_id: String, uom_llf: String, quantity_llf: Double,
                    unit_price_llf: Double, spend_type_name: String, description: String, commodity_id: String,
                    company_code_id: String, corp_high4: String, supplier_part_number: String, part_number: String,
                    supplier_id: String, supplier_name: String, csid_parent_name: String, end_date: Int, start_date: Int, oilPrice: Double)

case class LineItemGroup(id:Long, part_number: String, supplier_id:String, supplier_name:String,
                         group_size: Int,  mode: Double, average: Double, min_price: Double, max_price: Double,
                         deviation: Double, dispersion:Double, max_year:Int,
                         have_price_catalog:Boolean, have_non_catalog: Boolean, have_only_non_catalog: Boolean,
                         oil_and_gas_correlation:Double, seasonality_correlation:Double, group_overcharge: Double,
                         lowerBound: Double, upperBound: Double,
                         rate_of_change_rolling: Double, rate_of_change_benchmark: Double, exceeds_benchmark: String)

object AnomalyItemPriceDetectorPartNumber extends Serializable {
  import Functions._
  import SparkConnector.spark
  import spark.implicits._

  val START_DATE = 20150000
  val MIN_OVERCHARGE = 200

  def getOilAndGasCorrelation(items: Array[LineItem]) = {
    val (f1, f2) = items.map(li => (li.unit_price_llf, li.oilPrice)).filter(pr => pr._1 != null && pr._2 != null).unzip
    findSpearmansCorrelation(f1, f2)
  }

  def getSeasonalityCorrelation(items: Array[LineItem]) = {
    val prepared = items.map(li => (getSeason(li.start_date), li.unit_price_llf)).filter(_._1 != null).
      groupBy(_._1).
      map(li => li._2.map(_._2)).toList

    findOneWayAnovaCorrelation(prepared)
  }

  def findOutliersPerSubgroup(items: Array[LineItem]) = {
    if (items.length < 3) {
      (Double.MinValue, Double.MaxValue)
    } else {
      val sortedItems = items.sortBy(_.unit_price_llf)
      val length = sortedItems.length

      def medianOfArr(arr: Array[LineItem]): Double = {
        if (arr.length % 2 == 0) {
          (arr(arr.length / 2).unit_price_llf + arr(arr.length / 2 - 1).unit_price_llf) / 2
        } else {
          arr(arr.length / 2).unit_price_llf
        }
      }

      val Qmedian = medianOfArr(sortedItems)
      val Qlower = medianOfArr(sortedItems.slice(0, length / 2))
      val Qupper = if (length % 2 == 0) {
        medianOfArr(sortedItems.slice(length / 2, length))
      } else {
        medianOfArr(sortedItems.slice(length / 2 + 1, length))
      }

      val IQR = Qupper - Qlower
      val lowerBound = Qlower - 1.5 * IQR
      val upperBound = Qupper + 1.5 * IQR
      (lowerBound, upperBound)
    }
  }

  def calculateItemPotentialOvercharge(spend_type_name: String, start_date: Int, unit_price_llf: Double, amount:Double, basePrice: Double): Double ={
    if(spend_type_name != "Priced Catalog" && start_date > START_DATE){
      Math.max((unit_price_llf - basePrice) * amount, 0D)
    }else{
      0D
    }
  }

  val calculateItemPotentialOverchargeUDF = udf((spend_type_name: String, start_date: Int, unit_price_llf: Double, amount:Double, basePrice: Double) =>
    calculateItemPotentialOvercharge(spend_type_name, start_date, unit_price_llf, amount, basePrice))

  val percentile = new Percentile()
  def calculateGroupPotentialOvercharge(items: Array[LineItem], avg: Double): Double = {
    val medianValue = median(items.map(_.unit_price_llf))
    val lowerMedian = items.filter(_.unit_price_llf < medianValue).map(_.amount).size
    val lessPart = lowerMedian / items.size

    if(lessPart > 0.1) {
      val sortedPrices = items.map(_.unit_price_llf).sorted
      val p10 = percentile.evaluate(sortedPrices, 10)
      items.map(li => calculateItemPotentialOvercharge(li.spend_type_name, li.start_date, li.unit_price_llf, li.amount, medianValue + p10)).sum
    }
    else 0
  }

  def takeSample(a: Array[LineItem], maxLength: Int) = {
    val seed = a.size / maxLength
    if(seed > 0)
      a.zipWithIndex.filter(_._2 % seed == 0).map(_._1)
    else a
  }

  def calculateRollingYearPriceChangeRatePerSubgroup(items: Array[LineItem]) = {
    if(items.length > 1) {
      val newestLineItemDate = items.sortBy(_.start_date)(Ordering.Int.reverse).head.start_date
      val yearAgoFromNewestItemDate = newestLineItemDate - 10000
      val rollingYearPriceChanges = items.filter(_.start_date >= yearAgoFromNewestItemDate).map(_.unit_price_llf).distinct.length
      rollingYearPriceChanges
    } else 0
  }

  def calculateMeanOfPriceChangesPerSubgroup(items: Array[LineItem])  = {
    if(items.length > 1) {
      val itemsGroupedByYear = items.groupBy(item => item.start_date.toString.take(4)).values
      val totalPriceChanges = itemsGroupedByYear.map(itemsPerYear => itemsPerYear.map(_.unit_price_llf).distinct.length).sum
      val changeRatePerSubgroup = totalPriceChanges / itemsGroupedByYear.size.toDouble
      changeRatePerSubgroup
    } else 0D
  }

  def evaluateGroup(part_number:String, supplier_id:String, items: Array[LineItem]) = {
    val prices = items.map(_.unit_price_llf)
    val deviation = calculateDeviation(prices)
    val average = prices.sum / items.size
    val (lowerBound, upperBound) =  findOutliersPerSubgroup(items)
    val sample: Array[LineItem] = takeSample(items, 1000)
    val rollingYearPriceChangeRate = calculateRollingYearPriceChangeRatePerSubgroup(items)
    val meanOfPriceChanges = calculateMeanOfPriceChangesPerSubgroup(items)

    LineItemGroup(
      id = items.map(_.id).min,
      part_number = part_number,
      supplier_id = supplier_id,
      supplier_name = items.head.supplier_name,
      group_size = items.size,
      max_year = items.filter(_.spend_type_name == "Non-Catalog").map(_.start_date).foldLeft(0)((y1,y2) => if(y1>y2) y1 else y2),
      have_price_catalog = items.exists(_.spend_type_name != "Non-Catalog"),
      have_non_catalog = items.exists(_.spend_type_name == "Non-Catalog"),
      have_only_non_catalog = items.forall(_.spend_type_name == "Non-Catalog"),

      mode = findMode(prices),
      average = prices.sum / items.size,
      min_price = prices.min,
      max_price = prices.max,
      deviation = deviation,
      dispersion = deviation / average,

      oil_and_gas_correlation = getOilAndGasCorrelation(sample).getOrElse(0F),
      seasonality_correlation = getSeasonalityCorrelation(sample).getOrElse(0F),

      group_overcharge = calculateGroupPotentialOvercharge(items, average),

      lowerBound = lowerBound,
      upperBound = upperBound,
      rate_of_change_rolling = rollingYearPriceChangeRate,
      rate_of_change_benchmark = meanOfPriceChanges,
      exceeds_benchmark = if(rollingYearPriceChangeRate > meanOfPriceChanges) "Yes" else "No"
    )
  }

  def contracts = {
    val li = DataManager.get.vwDwepPoAndSeLineItemAnomalyDetector
    val contracts = DataManager.get.vwDwepContracts.select('contract_id, 'contract_owner_name)

    val sutableContracts = li.filter(
      to_utc_timestamp('source_approved_date.divide(1000).cast("timestamp"), "CST") >= to_date(lit("2016-10-01"))).
      groupBy('contract_id).
      agg(size(collect_set('ariba_doc_id)).as("invoice_amount"),
        count('ariba_doc_id).as("line_item_amount"),
        sum(when('spend_type_name === "Non-Catalog", lit(1)).otherwise(lit(0))).as("nc_line_item_amount"),
        sum('amount_after_discount_src).cast("long").as("total_spent")).
      withColumn("great_tatal_spent", sum('total_spent).over(Window.orderBy(lit(""))).cast("long")).
      withColumn("running_great_tatal_spent", sum('total_spent).
        over(Window.orderBy('total_spent.desc).rowsBetween(Long.MinValue, 0)).cast("long")).
      withColumn("running_total", count('total_spent).
        over(Window.orderBy('total_spent.desc).rowsBetween(Long.MinValue, 0))).
      withColumn("runningPercentage", 'running_great_tatal_spent.cast("Double") / 'great_tatal_spent).
      filter(('runningPercentage < 0.9 || 'running_total < 100) && 'invoice_amount < 500 && 'nc_line_item_amount < 2000).
      select('contract_id).as[String].collect()

    contracts.filter('contract_id.isin(sutableContracts:_*))
//    contracts
  }

  def containsThirdParty(columnName: Column) = coalesce(regexp_extract(lower(columnName), "(3|third)[rd ]{0,4}part", 0), lit("")) =!= ""

  val getHesh = udf((str:String) => str.hashCode)
  lazy val lineItem = {
    val wellNameMap = DataManager.get.wvJobAfe.join(DataManager.get.wvWellHeader, "idwell").
      select('wellname, regexp_replace(upper('afenumber), "[^0-9A-Z]", "").as("afe_number"))
    val li = DataManager.get.vwDwepPoAndSeLineItemAnomalyDetector.
      filter(!containsThirdParty('part_number) && !containsThirdParty('description) && 'start_date >= "20160000")

    li.
      join(wellNameMap, 'wbs_element_id === 'afe_number, "left").
      join(contracts, "contract_id").
      select('invoice_number, 'ariba_doc_id, 'wellname,'wbs_element_id,'contract_id,'contract_owner_name,'business_key,
        'account_id, 'uom_llf, coalesce('quantity_llf, lit(0)).as("quantity_llf").cast("Double"),
        coalesce('amount_after_discount_src, lit(0)).as("amount").cast("Double"),
        coalesce('unit_price_llf, lit(0)).as("unit_price_llf").cast("Double"), 'spend_type_name,
        'supplier_name_without_id.as("supplier_name"), 'description, 'commodity_id, 'company_code_id, 'corp_high4,
        'csid_parent_name, 'supplier_part_number, regexp_replace(lower('part_number), "[^0-9a-z]", "").as("part_number"),
        'local_ref_number, 'supplier_id, 'end_date.cast("Int"), 'start_date.cast("Int")
      ).withColumn("oilPrice", getOilPriceUDF('start_date)).
      withColumn("id", getHesh('business_key)).
      filter('part_number.isNotNull && 'unit_price_llf > 0).as[LineItem].cache()
  }

  lazy val calculate = {
    lineItem.groupByKey(li => (li.part_number, li.supplier_id)).
      mapGroups((k, v) => {
        val items = v.toArray
        (evaluateGroup(k._1, k._2, items), items)
      }).cache()
  }
}
