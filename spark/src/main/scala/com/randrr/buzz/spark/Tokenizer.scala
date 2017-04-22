package com.randrr.buzz.spark

object Tokenizer {
  def transform(phrase: String) = phrase.
    replaceAll("['`\\-]", "").
    split("[\\s&\\.]+").
    map(_.replaceAll("\\W", "").toLowerCase()).
    filter(_.nonEmpty)
}
