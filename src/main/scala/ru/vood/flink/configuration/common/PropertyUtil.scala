package ru.vood.flink.configuration.common

import java.util.Properties
import scala.util.{Failure, Success, Try}


object PropertyUtil extends Serializable {

  implicit val i: String => Int = { s: String => java.lang.Integer.parseInt(s) }
  implicit val l: String => Long = { s: String => java.lang.Long.parseLong(s) }
  implicit val bd: String => BigDecimal = { s => BigDecimal(s) }


  def metaPrefix(prf: String, clazz: Class[_])(implicit appPropImplicit: AllApplicationProperties): Option[String] = {
    val meta = clazz.getSimpleName
    val newPrf = s"$prf$meta."
    val stringToString = appPropImplicit.prop.keys.toList
      .filter(ent => {
        ent.contains(newPrf)
      }

      )
    if (stringToString.isEmpty)
      None
    else {
      val head1 = stringToString.head
      val indexMeta = head1.indexOf(meta)
      val resPrf = head1.substring(0, indexMeta) + meta
      Some(resPrf)
    }
  }

  def createByClass[T](prf: String, clazz: Class[_], init: String => Either[Errors, T])(implicit appProps: AllApplicationProperties): Either[Errors, T] = {
    createByClassOption(prf, clazz, init)
      .map(a => {
        val a1 = a
        a1.getOrElse(throw new IllegalStateException("must contains prefix " + prf + clazz.getSimpleName))
      })
  }

  def createByClassOption[T](prf: String, clazz: Class[_], init: String => Either[Errors, T])(implicit appProps: AllApplicationProperties): Either[Errors, Option[T]] = {
    val str = fullPrefix(prf)

    val stringToString = AllApplicationProperties(appProps.prop.filter(q => q._1.startsWith(str)))

    val prfOption = metaPrefix(str, clazz)(stringToString)
    val value = prfOption match {
      case Some(v) =>
        val errorsOrT = init(v)
        errorsOrT.map(d => Some(d))
      case None => Right(None)
    }
    value
  }

  def propertyVal[T](prefix: String, propName: String)(implicit appPropImplicit: AllApplicationProperties, strToT: String => T): Either[Errors, T] = {
    filterAndMap(fullPrefix(prefix), appPropImplicit.prop)
      .get(propName) match {
      case Some(v) =>
        Try {
          Right(strToT(v))
        } match {
          case Success(value) => value
          case Failure(exception) => Left(Errors(List(s"Unable to read property '$propName' with prefix '$prefix' error: ${exception.getMessage}")))
        }
      case None => Left(Errors(List(s"Unable to read property '$propName' with prefix '$prefix'")))
    }
  }

  def propertyValOptional[T](prefix: String, propName: String)(implicit appPropImplicit: AllApplicationProperties, strToT: String => T): Either[Errors, Option[T]] = {
    filterAndMap(fullPrefix(prefix), appPropImplicit.prop)
      .get(propName) match {
      case Some(v) =>
        Try {
          strToT(v)
        } match {
          case Success(value) => Right(Some(value))
          case Failure(exception) => Left(Errors(List(s"Unable to read property '$propName' with prefix '$prefix' error: ${exception.getMessage}")))
        }
      case None => Right(None)
    }
  }


  def asProperty(propPrefix: String = "")(implicit appPropImplicit: AllApplicationProperties):  Either[Errors, Properties]  = {
    val stringToString = filterAndMap(fullPrefix(propPrefix), appPropImplicit.prop)
    Right(getPropsFromMap(stringToString))
  }

  def mapProperty[T](prefix: String = "",
                     init: (String, AllApplicationProperties) => T
                    )(implicit appProps: AllApplicationProperties): Either[Errors, Map[String, T]] = {
    val prf = fullPrefix(prefix)

    val filteredProp = filterAndMap(prf, appProps.prop)
    val stringToT = filteredProp
      .groupBy({ entry => entry._1.split("\\.")(0) })
      .map({ entry =>
        val (key, uncookedVal) = entry
        val t =
          Try {
            init(key, AllApplicationProperties(uncookedVal))
          }
        (key, t)
      }
      ).span { s => s._2.isFailure }

    if (stringToT._1.isEmpty)
      Right(stringToT._2.collect { case (key: String, value: Success[T]) => key -> value.value })
    else Left(Errors(stringToT._1.collect { case (key: String, value: Failure[T]) => s"error by prefix-> '$prefix' with key-> '$key' error: ${value.exception.getMessage}" }.toList))
  }

  def fullPrefix(prefix: String): String =
    if (prefix == null || prefix.endsWith("."))
      prefix
    else s"$prefix."

  private def filterAndMap(prf: String, m: Map[String, String]): Map[String, String] = {
    if (prf == null) m
    else m.filter(entry => entry._1.startsWith(prf))
      .map(entry => (entry._1.replace(prf, ""), entry._2))
  }

  private def getPropsFromMap(props: Map[String, String]): Properties = {
    import scala.collection.JavaConverters.mapAsJavaMapConverter
    val properites = new Properties()
    properites.putAll(props.asJava)
    properites
  }

}
