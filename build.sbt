// val scala3Version = "3.1.2"
// ThisBuild / name := "swopen"
ThisBuild / organization           := "io.github.liewhite"
ThisBuild / organizationName       := "liewhite"
ThisBuild / version                := sys.env.get("RELEASE_VERSION").getOrElse("0.4.2")
ThisBuild / scalaVersion           := "3.1.2"
ThisBuild / versionScheme          := Some("early-semver")
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo              := sonatypePublishToBundle.value
sonatypeCredentialHost             := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"
conflictManager := ConflictManager.latestCompatible

lazy val shapelessDeps = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "shapeless3-deriving" % "3.0.3"
  )
)

lazy val log4jDeps = Seq(
  libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-simple" % "1.7.25"
  )
)

lazy val common = (project in file("common")).settings(
)

lazy val json = (project in file("json"))
    .settings(
      shapelessDeps,
      log4jDeps,
      libraryDependencies ++= Seq(
        "com.novocode" % "junit-interface" % "0.11" % "test",
        // "com.lihaoyi" %% "upickle" % "1.6.0",
        "dev.zio" % "zio-json_3" % "0.3.0-RC7",
      )
    )
    .dependsOn(common)


// lazy val sqlx_core = (project in file("sqlx_core"))
//     .settings(
//       shapelessDeps,
//       log4jDeps,
//       libraryDependencies += "org.jooq"       % "jooq"                 % "3.16.5",
//       libraryDependencies += "org.jooq"       % "jooq-meta"            % "3.16.5",
//       libraryDependencies += "mysql"          % "mysql-connector-java" % "8.0.28",
//       libraryDependencies += "org.postgresql" % "postgresql"           % "42.3.3",
//       libraryDependencies += "org.jetbrains"  % "annotations"          % "23.0.0",
//       libraryDependencies += "io.getquill"    % "quill-jdbc_3"         % "3.16.3.Beta2.5"
//     )
//     .dependsOn(common)


lazy val sqlx = (project in file("sqlx"))
    .settings(
      shapelessDeps,
      log4jDeps,
      libraryDependencies += "org.jooq"       % "jooq"                 % "3.16.5",
      libraryDependencies += "org.jooq"       % "jooq-meta"            % "3.16.5",
      libraryDependencies += "mysql"          % "mysql-connector-java" % "8.0.28",
      libraryDependencies += "org.postgresql" % "postgresql"           % "42.3.3",
      libraryDependencies += "org.jetbrains"  % "annotations"          % "23.0.0",
      libraryDependencies += "io.getquill"    % "quill-jdbc_3"         % "3.16.3.Beta2.5" exclude("com.lihaoyi", "geny_2.13")
    )
    .dependsOn(common)

lazy val config = (project in file("config"))
    .settings(
      log4jDeps,
      libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1"
    )
    .dependsOn(common, json)

lazy val web3 = (project in file("web3"))
    .settings(
      log4jDeps,
      libraryDependencies += "commons-codec"  % "commons-codec" % "1.15",
      libraryDependencies += "org.web3j"      % "core"          % "4.9.0",
      libraryDependencies += "org.scalactic" %% "scalactic"     % "3.2.11",
      libraryDependencies += "org.scalatest" %% "scalatest"     % "3.2.11" % "test"
    )
    .dependsOn(common,json,sqlx)

lazy val root = (project in file("."))
    .aggregate(json, common, web3, sqlx, config)
    .settings(
      // scalaVersion := scala3Version,
      publish / skip := true
    )
