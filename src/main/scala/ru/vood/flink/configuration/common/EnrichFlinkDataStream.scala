package ru.vood.flink.configuration.common

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.DataStream

object EnrichFlinkDataStream {

  implicit final class EnrichFlinkDataStreamSink[T: TypeInformation](private val selfStream: DataStreamSink[T]) /*extends AnyVal*/ {

    @inline def enrichName(name: String = "NoNamed"): DataStreamSink[T] =
      selfStream
        .name(name)
        .uid(name)
  }

  implicit final class EnrichFlinkDataStream[T: TypeInformation](private val selfStream: DataStream[T]) /*extends AnyVal*/ {

    @inline def enrichNameAndSetParallelism(name: String = "NoNamed", parallelism: Int): DataStream[T] = {
      require(parallelism > 0, s"Parallelism must be > 0, current value $parallelism")
      enrichName(name)
        .setParallelism(parallelism)
    }

    @inline def enrichName(name: String = "NoNamed"): DataStream[T] =
      selfStream
        .name(name)
        .uid(name)

    @inline def addFlinkMetric(name: String, isProd: Boolean = true): DataStream[T] = {
      require(name.nonEmpty, s"Metric name must be not empty")
      if (isProd) selfStream
      else selfStream
        .map(new MetricsMapService[T](name))
    }

  }


}
