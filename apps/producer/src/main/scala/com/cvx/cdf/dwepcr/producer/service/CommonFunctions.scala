package com.cvx.cdf.dwepcr.producer.service

import org.apache.spark.sql.DataFrame

object CommonFunctions  extends Serializable{
  def breakToTrainingTestValidation(training:Double, test:Double, validation:Double, frame: DataFrame) = {
    val splits = frame.randomSplitAsList(Array(training, test, validation), 40)
    (splits.get(0),splits.get(1),splits.get(2))
  }
}
