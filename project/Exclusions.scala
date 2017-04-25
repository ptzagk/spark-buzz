import sbt._

object Exclusions {
  lazy val all = Seq(
    ExclusionRule("org.slf4j", "slf4j-log4j12")
  )
}
