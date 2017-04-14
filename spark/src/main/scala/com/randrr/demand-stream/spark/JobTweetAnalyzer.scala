package com.randrr.demandstream.spark

import scala.util.Properties
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.rogach.scallop._
import com.typesafe.scalalogging.Logger
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
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

    // val schema = ScalaReflection.schemaFor[Tweet].dataType.asInstanceOf[StructType]

    val schema = new StructType().
      add("id_str", StringType).
      add("text", StringType).
      add("lang", StringType).
      add("truncated", BooleanType)

    val tweets = spark.
      readStream.
      format("kafka").
      option("kafka.bootstrap.servers", conf.kafkaBrokers()).
      option("subscribe", conf.kafkaTopic()).
      // json(). 
      load().
      // select($"value".cast("string").as("value")).
      select(from_json($"value".cast("string"), schema).alias("tweet")).
      // select("tweet.*").
      select("tweet.*")
      // as[Tweet]

    val query = tweets.
      // flatMap(_.split(" ")).
      groupBy("id_str").
      // groupByKey(_.id_str).
      count().
      writeStream.
      outputMode("complete").
      format("console").
      trigger(ProcessingTime("2 seconds")).
      // format("parquet").
      // option("path", "/Users/christian/dump").
      // option("checkpointLocation", "/Users/christian/checkpoint/").
      start()

    // val query = tweets.
    //   groupBy("lang").count().
    //   writeStream.
    //   outputMode("complete").
    //   format("console").
    //   option("path", "/Users/christian/dump").
    //   start()

    // val query2 = tweets.
    // ...
    // outputMode("complete").

    spark.streams.awaitAnyTermination()
  }
}
