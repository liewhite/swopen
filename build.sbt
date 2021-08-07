// ThisBuild / name := "swopen"
ThisBuild / organization := "io.github.liewhite"
ThisBuild / organizationName := "liewhite"
ThisBuild / version := sys.env.get("RELEASE_VERSION").get
ThisBuild / scalaVersion := "3.0.1"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / scalacOptions ++= Seq("-Xmax-inlines", "256")
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo := sonatypePublishToBundle.value
sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild/sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "org.typelevel" %% "shapeless3-deriving" % "3.0.2",
    "io.circe" % "circe-core_3" % "0.14.1",
    "io.circe" %% "circe-parser" % "0.14.1",
  ),
)

lazy val json = (project in file("json"))
  .settings(
    commonSettings,
  )

lazy val main = (project in file("main"))
  .settings(
    commonSettings,
    publish / skip := true,
  )
  .dependsOn(json)

lazy val root = (project in file("."))
  .aggregate(main, json).settings(
  publish / skip := true,
)