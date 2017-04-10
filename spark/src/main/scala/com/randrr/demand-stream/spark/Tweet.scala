package com.randrr.demandstream.spark

case class TweetHashtag(
 text: String)

case class TweetUrl(
 url: String,
 expanded_url: String)

case class TweetEntity(
  hashtags: Seq[TweetHashtag],
  urls: Seq[TweetUrl])

case class Tweet(
  id_str: String,
  created_at: String,
  timestamp_ms: String,
  text: String,
  lang: String,
  truncated: Boolean,
  entities: Seq[TweetEntity])
