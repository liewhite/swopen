ThisBuild / organization := "com.liewhite"
ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "3.0.1"
Global / scalacOptions ++= Seq("-Xmax-inlines","256")

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "com.novocode" % "junit-interface" % "0.11" % "test",
    "com.fasterxml.jackson.core"%"jackson-databind"%"2.12.3",
    "org.typelevel"%%"shapeless3-deriving"%"3.0.1",
    // "org.typelevel"%%"shapeless3-typeable"%"3.0.1",
    // "org.typelevel"%%"shapeless3-test"%"3.0.1",
  ),

  // libraryDependencies ++= Seq(
  // "org.postgresql" % "postgresql" % "42.2.8",
  // "io.getquill" %% "quill-jdbc" % "3.7.1.Beta1.1"
  // )
)

lazy val JsonToolbox = (project in file("json"))
  .settings(
    commonSettings
  )

lazy val main = (project in file("Main"))
  .settings(
    commonSettings
  ).dependsOn(JsonToolbox)

lazy val root = (project in file(".")).aggregate(main,JsonToolbox)
