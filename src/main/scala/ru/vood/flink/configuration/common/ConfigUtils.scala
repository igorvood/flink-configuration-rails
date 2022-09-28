package ru.vood.flink.configuration.common

import org.apache.flink.api.java.utils.ParameterTool
import org.slf4j.LoggerFactory

import java.util.Properties
import scala.collection.JavaConverters._
import scala.collection.convert.ImplicitConversions.`map AsScala`
import scala.io.Source
import scala.util.{Failure, Success, Try}

object ConfigUtils {
  private val logger = LoggerFactory.getLogger(getClass)

  @deprecated()
  def getDtoMap(fileName: String, delim: String = "::"): Map[String, Array[String]] = {
    getPropsFromResourcesFile(fileName) match {
      case Success(s) =>
        if (s.isEmpty) {
          logger.error("No parameters app.uaspdto.*")
          System.exit(1)
          Map[String, Array[String]]()
        } else s.map(m => (m._1, m._2.split(delim)))
      case Failure(e) =>
        logger.error(s"File $fileName not exist in resources, ${e.getMessage}")
        System.exit(1)
        Map[String, Array[String]]()
    }
  }

    @deprecated("use readAllPropsByProfile")
  def getPropsFromResourcesFile(fileName: String): Try[Map[String, String]] = Try {
    val props = ParameterTool.fromPropertiesFile(Thread.currentThread.getContextClassLoader.getResourceAsStream(fileName))
    props.getProperties.toMap.asInstanceOf[Map[String, String]]
  }

  @deprecated("use readAllPropsByProfile")
  def getPropsFromArgs(args: Array[String] = Array[String]()): Try[Map[String, String]] = Try {
    if (args == null || args.isEmpty) return Try(Map[String, String]())
    val props = ParameterTool.fromArgs(args)
    props.getProperties.toMap.asInstanceOf[Map[String, String]]
  }

  @deprecated("use readAllPropsByProfile")
  def getStringFromResourceFile(fileName: String): String = {
    val bufferSource = Source.fromURL(getClass.getClassLoader.getResource(fileName))
    val result = bufferSource.mkString
    bufferSource.close()
    result
  }

  def getSchemaKey(props: Map[String, String], propPrefix: String = "app"): String =
    props(s"$propPrefix.schema.name") + "-" + props(s"$propPrefix.schema.start.version")

  @deprecated("use readAllPropsByProfile")
  def getAllProps(args: Array[String] = Array[String](), fileName: String = "application.properties"): Map[String, String] = {
    getPropsFromResourcesFile(fileName).get ++
      getPropsFromArgs(args).get
  }

  implicit def readAllPropsByProfile(
                                      args: Array[String] = Array[String](),
                                      profile: String = "local",
                                      isExample: Boolean = false): AllApplicationProperties = {
    val argsProps: Map[String, String] = getPropsFromArgs(args).get
    val appProps = getPropsFromResourcesFile("application.properties").get
    val ex = if (isExample) ".ex" else ""
    val appPropsLocal: Map[String, String] = scala.util.Try(getPropsFromResourcesFile(s"application-$profile.properties$ex").get) match {
      case Success(x) => x
      case Failure(_) => Map()
    }
    AllApplicationProperties(appPropsLocal ++ appProps ++ argsProps)
  }


}
