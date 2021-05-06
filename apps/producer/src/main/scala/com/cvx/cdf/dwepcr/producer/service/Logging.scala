package com.cvx.cdf.dwepcr.producer.service

import org.apache.log4j.{LogManager, Logger}

trait Logging {
  protected val logger: Logger = LogManager.getLogger(getClass.getName)
}