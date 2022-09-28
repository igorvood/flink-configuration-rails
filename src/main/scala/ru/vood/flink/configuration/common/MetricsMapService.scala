package ru.vood.flink.configuration.common

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.metrics.Counter

class MetricsMapService[T](counterName: String) extends RichMapFunction[T,T] {
  @transient private var counter: Counter = _

  override def open(parameters: Configuration): Unit = {
    counter = getRuntimeContext
      .getMetricGroup
      .counter(counterName)
  }

  override def map(value: T): T = {
    counter.inc()
    value
  }
}
