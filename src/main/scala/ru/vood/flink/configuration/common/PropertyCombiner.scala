package ru.vood.flink.configuration.common

import PrefixProperty.PredefPrefix

trait PropertyCombiner[T] {


  def create(prf: String)(implicit appProps: AllApplicationProperties): Either[Errors, T]

  def apply(prefix: String)(implicit appProps: AllApplicationProperties): T =
    prefix createProperty { prf =>
      val errorsOrT = prf createProperty { p => create(p)(appProps) }
      val t = errorsOrT match {
        case Right(value) => value
        case Left(value) => throw new IllegalStateException(value.errors.mkString("\n"))
      }
      t
    }

}