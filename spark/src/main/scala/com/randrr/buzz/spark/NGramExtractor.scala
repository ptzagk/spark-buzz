package com.randrr.buzz.spark

object NGramExtractor {
  def transform(words: Seq[String], n:Int = 2): Seq[String] = {
    val ngrams = (0 to words.length - n).
      map(i => words.slice(i, i + n).mkString(" "))

    ngrams
  }

  def transform(words: Seq[String], ns: Seq[Int]): Seq[String] = {
    val sets = ns.map(n => transform(words, n))

    sets.foldLeft(Seq[String]())(_ ++ _)
  }
}
