import swopen.jsonSchema.JsonSchema.given
import swopen.jsonSchema.JsonSchema

case class A(a: Int) extends scala.annotation.Annotation
case class B(a: Int,b: Opt[Int])

enum Opt[+T]{
   case Sm(t: T)
   case NnE
}


@main def test(): Unit = {
   println(JsonSchema.schema[Opt.Sm[Int]])
}