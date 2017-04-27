package com.randrr.buzz.spark

object Tokenizer {
  def transform(phrase: Option[String]) = phrase.getOrElse("").
    replaceAll("['`\\-]", "").
    split("[\\s&\\.]+").
    map(_.replaceAll("\\W", "").toLowerCase()).
    filter(_.nonEmpty)
}
