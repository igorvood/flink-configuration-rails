package ru.vood.flink.configuration.configuration.property

import ru.vood.flink.configuration.common.{AllApplicationProperties, Errors, PropertyCombiner}
import ru.vood.flink.configuration.common.PropertyUtil.propertyVal

case class TestPropMeta(s: String,
                        ss: String
                       )


object TestPropMeta extends PropertyCombiner[TestPropMeta] {
  override def create(prf: String)(implicit appProps: AllApplicationProperties): Either[Errors, TestPropMeta] = {
    for {
      string <- propertyVal[String](prf, "s")
      string1 <- propertyVal[String](prf, "ss")
    } yield TestPropMeta(string ,string1)

  }
}