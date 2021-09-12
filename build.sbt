// ThisBuild / name := "swopen"
ThisBuild / organization := "io.github.liewhite"
ThisBuild / organizationName := "liewhite"
ThisBuild / version := sys.env.get("RELEASE_VERSION").getOrElse("0.4.2")
ThisBuild / scalaVersion := "3.0.1"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo := sonatypePublishToBundle.value
sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-Xmax-inlines", "256"),
  libraryDependencies ++= Seq(
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "org.typelevel" %% "shapeless3-deriving" % "3.0.2",
    "io.circe" % "circe-core_3" % "0.14.1",
    "io.circe" %% "circe-parser" % "0.14.1",
    "org.slf4j" % "slf4j-simple" % "1.7.25"
  )
)

lazy val common = (project in file("common")).settings(
)

lazy val json = (project in file("json"))
  .settings(
    commonSettings,
  )
  .dependsOn(common)

lazy val jsonContrib = (project in file("json_contrib"))
  .settings(
    commonSettings,
    libraryDependencies += "org.mongodb" % "mongodb-driver-sync" % "4.3.0",
  )
  .dependsOn(common,json)

lazy val main = (project in file("main"))
  .settings(
    commonSettings,
    libraryDependencies += "org.mongodb" % "mongodb-driver-sync" % "4.3.0",
    publish / skip := true
  )
  .dependsOn(json,jsonContrib)

lazy val root = (project in file("."))
  .aggregate(main, json,  common,  jsonContrib)
  .settings(
    publish / skip := true
  )
