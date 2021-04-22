import mill._, scalalib._

object JsonSchema extends ScalaModule {
    def scalaVersion = "3.0.0-RC3"
    def ivyDeps = Agg(
        ivy"org.typelevel:shapeless3-deriving_3.0.0-RC3:3.0.0-M3",
        ivy"org.typelevel:shapeless3-data_3.0.0-RC3:3.0.0-M3",
        ivy"org.typelevel:shapeless3-typeable_3.0.0-RC3:3.0.0-M3",
        ivy"org.typelevel:shapeless3-test_3.0.0-RC3:3.0.0-M3",
    )
}

object Main extends ScalaModule {
    def scalaVersion = "3.0.0-RC3"
    def moduleDeps = Seq(JsonSchema)
}