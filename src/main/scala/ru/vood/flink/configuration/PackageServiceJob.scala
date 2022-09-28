package ru.vood.flink.configuration

import org.slf4j.LoggerFactory
import ru.vood.flink.configuration.configuration.property.TestPropModel

object PackageServiceJob {

  private val logger = LoggerFactory.getLogger(getClass)


  def main(args: Array[String]): Unit = {
    logger.info("Start app: " + this.getClass.getName)

    val propsModel = TestPropModel.configApp(prf = "packageService", args = args)

    println(propsModel)

  }


}