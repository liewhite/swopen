import io.github.liewhite.config.Configable
import io.github.liewhite.json.JsonBehavior._

case class B(
  b: Boolean,
) derives Configable

case class A(
  port: Int,
  b: B,
  url: String = "xxxxx",
) derives Configable

@main def main = {
  val configs = Configable.defaultConfigsWithEnv()
  println(configs)
  println(Configable.configure[A](configs))

}