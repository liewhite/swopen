ThisBuild / organization := "com.github.liewhite"
ThisBuild / version      := "0.0.1"
ThisBuild / scalaVersion := "3.0.0-RC3"

lazy val commonSettings = Seq(
  libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
)

lazy val JsonToolbox = (project in file("JsonToolbox"))
  .settings(
    libraryDependencies ++= Seq(
        "com.fasterxml.jackson.core"%"jackson-databind"%"2.12.3",

        "org.typelevel"%%"shapeless3-deriving"%"3.0.0-M3",
        "org.typelevel"%%"shapeless3-data"%"3.0.0-M3",
        "org.typelevel"%%"shapeless3-typeable"%"3.0.0-M3",
    ),
    commonSettings
  )

lazy val main = (project in file("Main"))
  .settings(
    commonSettings
  ).dependsOn(JsonToolbox)