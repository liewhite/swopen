// ThisBuild / name := "swopen"
ThisBuild / organization := "io.github.liewhite"
ThisBuild / organizationName := "liewhite"
ThisBuild / version := sys.env.get("RELEASE_VERSION").getOrElse("0.4.2")
ThisBuild / scalaVersion := "3.1.0"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo := sonatypePublishToBundle.value
sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-Xmax-inlines", "256"),
  libraryDependencies ++= Seq(
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "org.typelevel" %% "shapeless3-deriving" % "3.0.3",
    "io.circe" % "circe-core_3" % "0.14.1",
    "io.circe" %% "circe-parser" % "0.14.1",
    "org.slf4j" % "slf4j-simple" % "1.7.25"
  )
)

lazy val common = (project in file("common")).settings(
  scalacOptions ++= Seq("-Xmax-inlines", "256"),
  libraryDependencies ++= Seq(
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "org.typelevel" %% "shapeless3-deriving" % "3.0.3",
    "io.circe" % "circe-core_3" % "0.14.1",
    "io.circe" %% "circe-parser" % "0.14.1",
    "org.slf4j" % "slf4j-simple" % "1.7.25"
  ),
  libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
)

lazy val json = (project in file("json"))
  .settings(
    commonSettings
  )
  .dependsOn(common)
lazy val sqlx = (project in file("sqlx"))
  .settings(
    commonSettings,
    libraryDependencies += "org.jooq" % "jooq" % "3.16.5",
    libraryDependencies += "org.jooq" % "jooq-meta" % "3.16.5",
    libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.28",
    libraryDependencies += "org.postgresql" % "postgresql" % "42.3.3",
    libraryDependencies += "org.jetbrains" % "annotations" % "23.0.0",
    libraryDependencies += "io.getquill" % "quill-jdbc_3" % "3.16.3.Beta2.5",
    libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.28"
  )
  .dependsOn(common)

lazy val config = (project in file("config"))
  .settings(
    commonSettings,
    libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1"
  )
  .dependsOn(common, json)

lazy val jsonContrib = (project in file("json_contrib"))
  .settings(
    commonSettings,
    libraryDependencies += "org.mongodb" % "mongodb-driver-sync" % "4.3.0"
  )
  .dependsOn(common, json)

lazy val web3 = (project in file("web3"))
  .settings(
    libraryDependencies += "commons-codec" % "commons-codec" % "1.15",
    libraryDependencies += "org.web3j" % "core" % "4.9.0",
    libraryDependencies += "io.getquill" % "quill-jdbc_3" % "3.16.3.Beta2.5",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.11",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"
  )
  .dependsOn(json, common,sqlx)

lazy val root = (project in file("."))
  .aggregate(json, common, jsonContrib, web3, sqlx)
  .settings(
    publish / skip := true
  )
