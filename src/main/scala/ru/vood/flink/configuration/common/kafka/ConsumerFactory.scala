package ru.vood.flink.configuration.common.kafka

import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, KafkaDeserializationSchema}

import java.util.Properties


object ConsumerFactory {
  def getKafkaConsumer[T](topic: String, des: DeserializationSchema[T], properties: Properties): FlinkKafkaConsumer[T] = {
    val consumer = new FlinkKafkaConsumer[T](topic, des, properties.clone.asInstanceOf[Properties])
    consumer.setStartFromGroupOffsets()
    consumer
  }

  def getKafkaConsumer[T](topic: String, des: KafkaDeserializationSchema[T], properties: Properties): FlinkKafkaConsumer[T] = {
    val consumer = new FlinkKafkaConsumer[T](topic, des, properties.clone.asInstanceOf[Properties])
    consumer.setStartFromGroupOffsets()
    consumer
  }

}
