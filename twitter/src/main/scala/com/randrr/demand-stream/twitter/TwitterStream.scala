package com.randrr.demandstream.twitter

import scala.util.Properties
import collection.JavaConverters._
import java.util.concurrent.LinkedBlockingQueue
import com.typesafe.scalalogging.Logger
import org.rogach.scallop._
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.{Client,Constants}
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.OAuth1

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val kafkaBrokers = opt[String]()
  val kafkaTopic = opt[String]()
  val tweetFilters = opt[List[String]]()

  verify()
}

object TwitterStream {

  val appName = getClass.getSimpleName

  def main(args: Array[String]) = {
    val conf = new Conf(args)

    val logger = Logger(appName)

    val kafkaBrokers = conf.kafkaBrokers()
    val kafkaTopic = conf.kafkaTopic()
    val tweetFilters = conf.tweetFilters()

    val kafkaSink = new KafkaSink(appName, kafkaBrokers, kafkaTopic)

    val endpoint = new StatusesFilterEndpoint()  

    logger.info(s"initializing twitter client with track filters - ${tweetFilters.mkString(",")}")

    endpoint.trackTerms(tweetFilters.asJava)     

    val (queue, processor) = createProcessor()

    val client = new ClientBuilder().
      name(appName).
      hosts(Constants.STREAM_HOST).
      endpoint(endpoint).
      authentication(createAuthentication()).
      processor(processor).
      build()
    
    client.connect()
    
    while (!client.isDone()) {
      val message = queue.take()
      kafkaSink.writeTweet(message)
    }
  }

  def createAuthentication(): OAuth1 = new OAuth1(
    Properties.envOrElse("TWITTER_CONSUMER_KEY", ""),
    Properties.envOrElse("TWITTER_CONSUMER_SECRET", ""),
    Properties.envOrElse("TWITTER_ACCESS_TOKEN", ""),
    Properties.envOrElse("TWITTER_TOKEN_SECRET", "")
  )

  def createProcessor(): (LinkedBlockingQueue[String], StringDelimitedProcessor) = { 
    val queue = new LinkedBlockingQueue[String](100000)
    val processor = new StringDelimitedProcessor(queue) 

    (queue, processor)
  }
}
