package ru.vood.flink.configuration.common

trait ConfigurationInitialise[CONFIGURATION] {

  def configApp(prf: String, args: Array[String]): CONFIGURATION =
    defaultConfiguration(prf)(ConfigUtils.readAllPropsByProfile(args))

  def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties): CONFIGURATION

}

object ConfigurationInitialise {
  implicit val argsToPropFun: Array[String] => AllApplicationProperties = { arg =>
    ConfigUtils.readAllPropsByProfile(arg)
  }
}
