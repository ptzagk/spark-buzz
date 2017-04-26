package com.randrr.buzz.spark

import scala.util.Properties
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.StructType
import org.rogach.scallop._
import com.typesafe.scalalogging.Logger
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.streaming.OutputMode

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val kafkaBrokers = opt[String](required=true)
  val kafkaTopic = opt[String](required=true)

  verify()
}

object TweetAnalyzer {
  val checkpointDir = scala.util.Properties.envOrElse("BUZZ_DIR_CHECKPOINTS", "")
  val dataDir = scala.util.Properties.envOrElse("BUZZ_DIR_DATA", "")

  val companiesDataFile = new Path(dataDir, "companies.txt").toString()
  
  val allTweetsDataDir = new Path(dataDir, "all_tweets").toString()
  val allTweetsCheckpointDir = new Path(checkpointDir, "all_tweets").toString()
  val companyCountsDataDir = new Path(dataDir, "company_counts").toString()
  val companyCountsCheckpointDir = new Path(checkpointDir, "company_counts").toString()
  val ngramCountsDataDir = new Path(dataDir, "ngram_counts").toString()
  val ngramCountsCheckpointDir = new Path(checkpointDir, "ngram_counts").toString()

  val appName = getClass.getSimpleName

  def main(args: Array[String]) = {
    val conf = new Conf(args)
    val logger = Logger(appName)

    logger.info(s"Launching $appName with checkpoints at $checkpointDir and data at $dataDir")

    logger.info(s"allTweetsDataDir is $allTweetsDataDir")
    logger.info(s"allTweetsCheckpointDir is $allTweetsCheckpointDir")
    logger.info(s"companyCountsDir is $companyCountsDataDir")
    logger.info(s"companyCountsCheckpointDir is $companyCountsCheckpointDir")
    logger.info(s"ngramCountsDir is $ngramCountsDataDir")
    logger.info(s"ngramCountsCheckpointDir is $ngramCountsCheckpointDir")

    val spark = SparkSession.
      builder.
      appName(appName).
      getOrCreate()

    import spark.implicits._

    val schema = ScalaReflection.schemaFor[Tweet].dataType.asInstanceOf[StructType]

    val tweets = spark.
      readStream.
      format("kafka").
      option("kafka.bootstrap.servers", conf.kafkaBrokers()).
      option("subscribe", conf.kafkaTopic()).
      load().
      select(from_json($"value".cast("string"), schema).alias("tweet")).
      select("tweet.*").
      as[Tweet]

    val allTweetsOutput = tweets.
      writeStream.
      outputMode(OutputMode.Append()).
      format("json").
      option("path", allTweetsDataDir).
      option("checkpointLocation", allTweetsCheckpointDir).
      start()
 
    val ngrams = tweets.
      map(x => Tokenizer.transform(x.text)).
      map(x => StopWordsRemover.transform(x)).
      map(x => NGramExtractor.transform(x, Seq(1, 2, 3, 4)))

    val ngramCounts = ngrams.
      flatMap(x => x).toDF("text").
      groupBy(col("text")).count().
      select($"text", $"count".alias("num")).as[TextCount]

    val ngramCountsOutput = ngramCounts.
      writeStream.
      foreach(new WordCountWriter(ngramCountsDataDir)).
      outputMode(OutputMode.Complete()).
      trigger(ProcessingTime("15 seconds")).
      option("checkpointLocation", ngramCountsCheckpointDir).
      start()

    val companies = spark.sparkContext.textFile(companiesDataFile).toDF("text")
    
    val companyCounts = ngrams.
      flatMap(x => x).toDF("text").
      join(companies, "text").
      groupBy(col("text")).count().
      select($"text", $"count".alias("num")).as[TextCount]

    val companyCountsOutput = companyCounts.
      writeStream.
      foreach(new WordCountWriter(companyCountsDataDir)).
      outputMode(OutputMode.Complete()).
      trigger(ProcessingTime("30 seconds")).
      option("checkpointLocation", companyCountsCheckpointDir).
      start()

    spark.streams.awaitAnyTermination()
  }
}
