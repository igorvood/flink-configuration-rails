package ru.vood.flink.configuration.configuration.property

import ru.vood.flink.configuration.common.{AllApplicationProperties, ConfigurationInitialise, Errors, PropertyCombiner}
import ru.vood.flink.configuration.common.PropertyUtil.{createByClass, createByClassOption, mapProperty}

case class TestPropModel(testProp: TestProp,
                         testPropMetaOptional: Option[TestPropMeta],
                         testPropMeta: TestPropMeta,
                         map: Map[String, TestProp]

                        )


object TestPropModel extends PropertyCombiner[TestPropModel] with ConfigurationInitialise[TestPropModel] {


  override def create(prf: String)(implicit appProps: AllApplicationProperties): Either[Errors, TestPropModel] = {
//    val errorsOrOption = createByClassOption(prf, TestPropMeta.getClass, TestPropMeta.create)
    for {
      t <- TestProp.create(prf)
      qq <- createByClassOption(prf, TestPropMeta.getClass, TestPropMeta.create)
      qqw <- createByClass(s"$prf.mandatory", TestPropMeta.getClass, TestPropMeta.create)
      map <- mapProperty(s"$prf.map.", { (str, appProps) => TestProp(str)(appProps) })
    } yield TestPropModel(t, qq, qqw, map)

  }

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties): TestPropModel = TestPropModel(prf)


}
