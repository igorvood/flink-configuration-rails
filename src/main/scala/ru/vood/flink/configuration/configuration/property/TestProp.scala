package ru.vood.flink.configuration.configuration.property

import ru.vood.flink.configuration.common.{AllApplicationProperties, Errors, PropertyCombiner}
import ru.vood.flink.configuration.common.PropertyUtil._

case class TestProp(int: Int,
                    string: String,
                    long: Long,
                    longOpt: Option[Long]
                   ) {
  require(int > 0, s"property 'int' must be > 0 ")
}

object TestProp extends PropertyCombiner[TestProp] {

  override def create(prf: String)(implicit appProps: AllApplicationProperties): Either[Errors, TestProp] = {
    for {
      int <- propertyVal[Int](prf, "int")
      string <- propertyVal[String](prf, "string")
      long <- propertyVal[Long](prf, "long")
      longq <- propertyValOptional[Long](prf, "longOpt")
    } yield new TestProp(int, string, long, longq)


  }
}