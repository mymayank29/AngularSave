package com.cvx.cdf.dwepcr.producer.contract.analyzer

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature.{CountVectorizer, IDF}
import org.apache.spark.sql.functions.udf

object CommonFunctions extends Serializable {
  import DateFromStringExtractor._

  val AMENDMENTS_LOOKUP_PREFIX = 1000
  val DAT_PREFIX_KEY_WORDS = Seq("as of", "entered into effective", "dated", "is made and")
  val AMENDMENT_KEY_WORDS = Seq("amendment")
  val NON_AMENDMENT_TITLE_KEY_WORDS = Seq("approval", "work_request")
  val NON_AMENDMENT_CONTENT_KEY_WORDS = Seq("you", "dear")
  val CONTRACT_NUMBER_PATTERM_1  = "CW.{1}?[0-9]*+".r
  val CONTRACT_NUMBER_PATH_PATTERM  = ".*?(CW.{1}?[0-9]*+.*)".r

  val getRelatedPath = (path:String) => {
    CONTRACT_NUMBER_PATH_PATTERM.findFirstMatchIn(path) match {
      case Some(gr) => "/" + gr.group(1)
      case _ => path
    }
  }

  val dateFromTitle = (str: String) => {
    getFirstDate(str)
  }

  val dateFromText = (words: Seq[String]) => {
    val head = words.take(AMENDMENTS_LOOKUP_PREFIX).mkString(" ")
    getFirstDate(head)
  }

  val containsAmendment = (words: String) => {
    AMENDMENT_KEY_WORDS.exists(words.contains)
  }


  val getSubArray = (text: Seq[String], length:Int) => {
    text.take(length)
  }

  val contractNumberFromPath = (str: String) => {
    CONTRACT_NUMBER_PATTERM_1.findFirstIn(str).getOrElse("")
  }

  val nonAmendmentWords = (title:String, words: String) => {
    val fileName = title.toLowerCase.split("/").last
    NON_AMENDMENT_TITLE_KEY_WORDS.exists(fileName.contains) ||
      NON_AMENDMENT_CONTENT_KEY_WORDS.exists(words.contains)
  }

  val dateFromTitleUDF = udf(dateFromTitle)
  val dateFromTextUDF = udf(dateFromText)
  val contractNumberFromPathUDF = udf(contractNumberFromPath)
  val nonAmendmentWordsUDF = udf(nonAmendmentWords)
  val getSubArrayUDF = udf(getSubArray)
  val containsAmendmentUDF = udf(containsAmendment)
  val getRelatedPathUDF = udf(getRelatedPath)

  def createPipeline(inputCol:String, label: String) : Pipeline = {
    val vectorizerModel = new CountVectorizer()
      .setInputCol(inputCol)
      .setOutputCol("vector_v1")
      .setVocabSize(Int.MaxValue)
      .setMinDF(3)

    val idfModel = new IDF().setInputCol("vector_v1").setOutputCol("features")

    val classifier = new RandomForestClassifier().
      setMaxDepth(5).
      setNumTrees(40).
      setSeed(107).
      setLabelCol(label).
      setFeaturesCol("features")

    new Pipeline().setStages(Array(vectorizerModel, idfModel, classifier))
  }

}
