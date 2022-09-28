package ru.vood.flink.configuration.common

import scala.util.{Failure, Success, Try}

object PrefixProperty {

  implicit final class PredefPrefix(val prf: String) extends AnyVal {

    @inline def createProperty[T](function: String => T): T = {
      val triedT = Try {
        function(prf)
      } match {
        case Success(value) => value
        case Failure(exception) => throw new IllegalArgumentException(s"Unable to read property by prefix '$prf', error: ${exception.getMessage}", exception)
      }
      triedT
    }

  }
}
