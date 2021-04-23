import mill._, scalalib._

object JsonToolbox extends ScalaModule {
    def scalaVersion = "3.0.0-RC3"
    def ivyDeps = Agg(
        ivy"com.googlecode.json-simple:json-simple:1.1.1",

        ivy"org.typelevel::shapeless3-deriving:3.0.0-M3",
        ivy"org.typelevel::shapeless3-data:3.0.0-M3",
        ivy"org.typelevel::shapeless3-typeable:3.0.0-M3",
        ivy"org.typelevel::shapeless3-test:3.0.0-M3",
    )
}

object Main extends ScalaModule {
    def scalaVersion = "3.0.0-RC3"
    def moduleDeps = Seq(JsonToolbox)
    def ivyDeps = Agg(
        ivy"org.typelevel::shapeless3-deriving:3.0.0-M3",
        ivy"org.typelevel::shapeless3-data:3.0.0-M3",
        ivy"org.typelevel::shapeless3-typeable:3.0.0-M3",
        ivy"org.typelevel::shapeless3-test:3.0.0-M3",
    )
}