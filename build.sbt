import sbtassembly.AssemblyPlugin.defaultShellScript
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
    name := "jobbuzz-twitter",
    mainClass in assembly := Some("com.randrr.buzz.twitter.TwitterStream"),
    assemblyJarName in assembly := s"${name.value}.jar",
    libraryDependencies += hbcCore,
    libraryDependencies += kafka,
    libraryDependencies += scallop,
    libraryDependencies += scalaLogging,
    libraryDependencies += logback,
    libraryDependencies += scalaTest,
    libraryDependencies ~= { _.map(_.excludeAll(Exclusions.all: _*)) }
  )

lazy val spark = (project in file("spark")).
  settings(
    commonSettings,
    name := "jobbuzz-spark",
    mainClass in assembly := Some("com.randrr.buzz.spark.TweetAnalyzer"),
    libraryDependencies += hbcCore,
    assemblyJarName in assembly := s"${name.value}.jar",
    assemblyExcludedJars in assembly := {
      val cp = (fullClasspath in assembly).value
      cp filter { el =>
        (el.data.getName == "unused-1.0.0.jar") ||
        (el.data.getName == "spark-tags_2.11-2.1.0.jar")  
      }
    },
    libraryDependencies += sparkCore,
    libraryDependencies += sparkSql,
    libraryDependencies += sparkSqlKafka,
    libraryDependencies += commonsCsv,
    libraryDependencies += scallop,
    libraryDependencies += scalaLogging,
    libraryDependencies += logback,
    libraryDependencies += scalaTest,
    libraryDependencies ~= { _.map(_.excludeAll(Exclusions.all: _*)) }
  )

lazy val root = (project in file(".")).
  settings(commonSettings).
  aggregate(twitter, spark)

