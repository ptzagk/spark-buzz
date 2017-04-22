import sbt._

object Dependencies {
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.1.7"
  lazy val scallop = "org.rogach" %% "scallop" % "2.1.1"
  lazy val hbcCore = "com.twitter" % "hbc-core" % "2.2.0"
  lazy val kafka = "org.apache.kafka" %% "kafka" % "0.10.2.0"
  lazy val commonsCsv = "org.apache.commons" % "commons-csv" % "1.4"
  lazy val sparkCore = "org.apache.spark" %% "spark-core" % "2.1.0"
  lazy val sparkSql = "org.apache.spark" %% "spark-sql" % "2.1.0"
  lazy val sparkSqlKafka = "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.1.0"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
}
