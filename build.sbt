import Dependencies._

lazy val commonSettings = Seq(
    organization := "com.randrr",
    // scalaVersion := "2.12.1",
    scalaVersion := "2.11.8",
    version      := "0.1.0-SNAPSHOT"
  )

lazy val twitter = (project in file("twitter")).
  settings(
    commonSettings,
    name := "Twitter",
    libraryDependencies += hbcCore,
    libraryDependencies += kafka,
    libraryDependencies += scallop,
    libraryDependencies += scalaLogging,
    libraryDependencies += logback,
    libraryDependencies += scalaTest % Test,
    libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-log4j12")) }
  )

lazy val spark = (project in file("spark")).
  settings(
    commonSettings,
    name := "Spark",
    libraryDependencies += sparkCore,
    libraryDependencies += sparkSql,
    libraryDependencies += sparkSqlKafka,
    libraryDependencies += scallop,
    libraryDependencies += scalaLogging,
    libraryDependencies += logback,
    libraryDependencies += scalaTest % Test,
    libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-log4j12")) }
  )

lazy val root = (project in file(".")).
  settings(commonSettings).
  aggregate(twitter, spark)

