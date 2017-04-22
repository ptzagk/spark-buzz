package com.randrr.buzz.spark

case class Tweet(
  id_str: String,
  created_at: String,
  timestamp_ms: String,
  text: String,
  lang: String)
