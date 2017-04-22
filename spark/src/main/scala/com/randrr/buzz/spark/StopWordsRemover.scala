package com.randrr.buzz.spark

import scala.io.Source

object StopWordsRemover {
  val stopWords = Source.
    fromInputStream(getClass.getResourceAsStream("/stopwords.txt")).
    getLines().toSeq

  def transform(words: Seq[String]) = words.filterNot(w => stopWords.contains(w))
}
