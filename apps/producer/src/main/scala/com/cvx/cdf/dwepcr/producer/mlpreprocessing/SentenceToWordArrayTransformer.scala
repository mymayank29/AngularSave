package com.cvx.cdf.dwepcr.producer.mlpreprocessing

import org.apache.spark.ml.feature.{StopWordsRemover, Tokenizer}
import org.apache.spark.sql.{Column, DataFrame}
import org.apache.spark.sql.functions.{lit, udf}

import scala.collection.mutable.ListBuffer


object SentenceToWordArrayTransformer extends Serializable{

  val stemmer = (word: String) => {
    word.replaceAll("(ies|ied|y|ing|s|es|e|ed|'s)$","")
  }

  val sentenceCleaner = (str: String) => {
    if(str == null)
      ""
    else
      str.
        toLowerCase.
        replaceAll("\\d+", "_").
        replaceAll("\\s+", " ").
        replaceAll("\\p{P}+", " ").
        split(" ").
        filter(word => !word.isEmpty && word.length>1).
        map(stemmer).
        mkString(" ")
  }

  val stringStemmer = (str: String) => {
    if(str == null)
      ""
    else {
      val words = str.split("\\W+")
      var res = new ListBuffer[String]()
      for(word <- words) {
        res += PorterStemmer.stem(word.toLowerCase())
      }
      res.mkString(" ")
    }
  }

  def transform(df: DataFrame, inputColumn: String, outputColumn: String):DataFrame = {
    val tempCol1 = s"${inputColumn}_1"
    val tempCol2 = s"${inputColumn}_2"

//    val sentenceCleanerUDF = udf(sentenceCleaner)
    val stringStemmerUDF = udf(stringStemmer)
    val stemmed = df.withColumn(tempCol1, stringStemmerUDF(new Column(inputColumn)))

    val tokenizer = new Tokenizer().setInputCol(tempCol1).setOutputCol(tempCol2)
    val remover = new StopWordsRemover().setInputCol(tempCol2).setOutputCol(outputColumn)

    stemmed.
      transform(tokenizer.transform).
      transform(remover.transform).
      drop(tempCol1, tempCol2)
  }
}
