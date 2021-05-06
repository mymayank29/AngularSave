package com.cvx.cdf.dwepcr.producer.mlpreprocessing

import com.cvx.cdf.dwepcr.producer.service.Config._
import com.cvx.cdf.dwepcr.producer.service.{CommonUDF, SparkConnector}
import org.apache.spark.ml.feature._
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame}

object TextSimilarityComparator extends Serializable{
  import CommonUDF._
  import SparkConnector.spark
  import spark.implicits._

  def prepareDataFrame(df: DataFrame, cl1: String, cl2: String):DataFrame = {
    val cleaned = df.
      withColumn("cl1", col(cl1)).withColumn("cl2", col(cl2)).
      transform(SentenceToWordArrayTransformer.transform(_, "cl1", "cl1_v")).
      transform(SentenceToWordArrayTransformer.transform(_, "cl2", "cl2_v")).cache()

    def distinctWordsDF(df: DataFrame, cn: String):DataFrame = {
      cleaned.select(cn).withColumn("word", explode(new Column(cn))).drop(cn).distinct()
    }

    val commonWords = cleaned.transform(distinctWordsDF(_, "cl1_v")).
      join(cleaned.transform(distinctWordsDF(_, "cl2_v")), "word").as[String].collect()
    val bcCommonWords = spark.sparkContext.broadcast(commonWords)
    val onlyCommonWordsUDF = udf((words:Seq[String]) => words.intersect(bcCommonWords.value))
    cleaned.
      withColumn("cl1_v", onlyCommonWordsUDF(col("cl1_v"))).
      withColumn("cl2_v", onlyCommonWordsUDF(col("cl2_v"))).cache()
  }

  def createPipeline(inputCol:String, outputCol:String) : Pipeline = {
    val countVectorizer = new CountVectorizer().setInputCol(inputCol).setOutputCol(s"${inputCol}label").setVocabSize(1000)
    val idf = new IDF().setInputCol(s"${inputCol}label").setOutputCol(outputCol)
    new Pipeline().setStages(Array(countVectorizer, idf))
  }

  def dropAllExcept(df:DataFrame, columns:String*): DataFrame ={
    val columnsForDrop = df.columns.filter(!columns.contains(_))
    df.drop(columnsForDrop:_*)
  }

  def produceModel(df: DataFrame, cl1: String, cl2: String, modelName: String) : PipelineModel = {
    val preprocessed = prepareDataFrame(df, cl1, cl2)
    val dfForTraining = preprocessed.select(col("cl1_v").as("col")).
      union(preprocessed.select(col("cl2_v").as("col"))).distinct()
    val cl1IdfModel = createPipeline("col", "idf").fit(dfForTraining)
    cl1IdfModel
//    TODO   Commented due to inability to save trained models in UAT
//    cl1IdfModel.save(s"${Config.modelsPath}/${modelName}")
  }

  def prepareCostGenDescrToContractData(df: DataFrame, costGenDescription: String): DataFrame ={
    val costGenDescriptionLimited = when(length(col(costGenDescription)) > 110, lit("") ).otherwise(col(costGenDescription))
    df.withColumn("costGenDescSentence", explode(split(costGenDescriptionLimited, "\\.")))
  }

  def produceContractToCostGenSimilarityModel(df: DataFrame, costGenDescription: String, contractTitle: String, modelName: String) = {
    val preparedDF = prepareCostGenDescrToContractData(df, costGenDescription)
    produceModel(preparedDF, contractTitle, "costGenDescSentence", modelName)
  }

  def findContractToCostGenSimilarity(df: DataFrame,  costGenDescription: String, contractTitle: String, cosSimCostDescr:String, modelName: String) : DataFrame = {
    val preparedDF = prepareCostGenDescrToContractData(df, costGenDescription)
    val columns = df.schema.map(c => col(c.name))
    val resDF = findSimilarity(preparedDF,"costGenDescSentence", contractTitle, cosSimCostDescr, modelName)
    resDF.groupBy(columns:_*).agg(max(col(cosSimCostDescr)) as cosSimCostDescr)
  }

  def findSimilarity(df: DataFrame, cl1: String, cl2: String, outCol:String, modelName: String):DataFrame = {
    val preprocessed = prepareDataFrame(df, cl1, cl2)
// TODO   Commented due to inability to save trained models in UAT
//    val idfModel = PipelineModel.load(s"${Config.modelsPath}/${modelName}")
    val idfModel = produceModel(df, cl1, cl2, modelName)
    val baseColumns = preprocessed.columns
    val result = preprocessed.
      withColumn("col", col("cl1_v")).
      transform(idfModel.transform).
      withColumn("cl1_idf", 'idf).transform(dropAllExcept(_,"cl1_idf" :: baseColumns.toList:_*)).
      withColumn("col", col("cl2_v")).
      transform(idfModel.transform).
      withColumn("cl2_idf", 'idf).
      withColumn(s"${outCol}", cosineSimilarityUdf(col("cl1_idf"), col("cl2_idf"))).cache()
    result.transform(dropAllExcept(_, outCol :: df.columns.toList:_*))
  }

  def findInvoiceDescToNPTCommentSimilarity(df: DataFrame, inv_description: String , npt_description: String, inv_to_npt_similarity: String,
                                            inv_to_cost_gen_simil: String, output_col: String) : DataFrame = {
    df.transform(findJaccardSimilarity(_, inv_description, npt_description, output_col)).
      withColumn(output_col,
        when(col(inv_to_cost_gen_simil) > costGenmatchTreshold || col(inv_to_npt_similarity) > nptMatchTreshold,
          col(output_col)).otherwise(lit(0)))

  }

  def findJaccardSimilarity(df: DataFrame, inv_description: String, npt_description: String, output_col: String) : DataFrame = {
    df.
      withColumn("inv_description", col(inv_description)).withColumn("npt_description", col(npt_description)).
      transform(SentenceToWordArrayTransformer.transform(_, "inv_description", "cl1_v")).
      transform(SentenceToWordArrayTransformer.transform(_, "npt_description", "cl2_v")).cache().
      withColumn(output_col, jaccardSimilarityUdf(col("cl1_v"), col("cl2_v"))).
      transform(dropAllExcept(_, output_col :: df.columns.toList:_*))
  }
}