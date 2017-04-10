package com.randrr.demandstream.spark

import scala.util.Properties
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.rogach.scallop._
import com.typesafe.scalalogging.Logger
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val kafkaBrokers = opt[String]()
  val kafkaTopic = opt[String]()

  verify()
}

object JobTweetAnalyzer {
  val appName = getClass.getSimpleName

  def main(args: Array[String]) = {
    val conf = new Conf(args)
    val logger = Logger(appName)
    
    val spark = SparkSession.
      builder.
      appName(appName).
      master("local[4]").
      getOrCreate()

    import spark.implicits._

    val schema = ScalaReflection.schemaFor[Tweet].dataType.asInstanceOf[StructType]

    val tweets = spark.
      readStream.
      format("kafka").
      option("kafka.bootstrap.servers", conf.kafkaBrokers()).
      option("subscribe", conf.kafkaTopic()).
      load().
      // selectExpr("CAST(value as STRING)")
      selectExpr("CAST(value as STRING)").
      select(from_json($"value", schema)).
      as[Tweet]

    val query = tweets.
      groupBy("timestamp_ms").
      count().
      writeStream.
      outputMode("complete").
      format("console").
      start()

    spark.streams.awaitAnyTermination()
  }
}
