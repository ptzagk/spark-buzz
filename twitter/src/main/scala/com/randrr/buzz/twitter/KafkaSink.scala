package com.randrr.buzz.twitter

import java.util.{Properties}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, Callback, RecordMetadata}
import com.typesafe.scalalogging.Logger

class KafkaSink(appName: String, brokers: String, topic: String) {
  val producer = createProducer()
  val logger = Logger(getClass.getSimpleName)

  def writeTweet(tweet: String): Unit = {
    val record = new ProducerRecord[String, String](topic, tweet)  

    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception != null) {
          logger.error("exception in producer callback", exception)
        } else {
          logger.info("producer send acknowledged")
        }
      }
    })
  }

  private def createProducer() = {
    val properties = new Properties()

    properties.put("bootstrap.servers", brokers)
    properties.put("client.id", appName)
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](properties)

    producer
  }
}
