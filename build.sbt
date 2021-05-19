ThisBuild / organization := "com.github.liewhite"
ThisBuild / version      := "0.0.1"
ThisBuild / scalaVersion := "3.0.0"

lazy val commonSettings = Seq(
  libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
)

lazy val JsonToolbox = (project in file("JsonToolbox"))
  .settings(
    libraryDependencies ++= Seq(
        "com.fasterxml.jackson.core"%"jackson-databind"%"2.12.3",

        // "org.typelevel"%"shapeless3-deriving_3.0.0-RC3"%"3.0.0-M4",
        // "org.typelevel"%"shapeless3-data_3.0.0-RC3"%"3.0.0-M4",
        // "org.typelevel"%"shapeless3-typeable_3.0.0-RC3"%"3.0.0-M4",
        // "org.typelevel"%"shapeless3-test_3.0.0-RC3"%"3.0.0-M4",
    ),
    commonSettings
  )

lazy val main = (project in file("Main"))
  .settings(
    libraryDependencies ++= Seq(
        "com.fasterxml.jackson.core"%"jackson-databind"%"2.12.3",

        // "org.typelevel"%"shapeless3-deriving_3.0.0-RC3"%"3.0.0-M4",
        // "org.typelevel"%"shapeless3-data_3.0.0-RC3"%"3.0.0-M4",
        // "org.typelevel"%"shapeless3-typeable_3.0.0-RC3"%"3.0.0-M4",
        // "org.typelevel"%"shapeless3-test_3.0.0-RC3"%"3.0.0-M4",
    ),
    commonSettings
  ).dependsOn(JsonToolbox)

lazy val root = (project in file(".")).aggregate(main,JsonToolbox)
