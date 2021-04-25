import swopen.jsonToolbox.JsonBehavior.encode
import swopen.jsonToolbox.JsonBehavior.decode
import swopen.jsonToolbox.json.{Json,JsonNumber}
import swopen.jsonToolbox.modifier.Modifier
import swopen.jsonToolbox.codec.Encoder
import scala.io.Source
import scala.math.BigInt
import shapeless3.deriving.{Annotation,summon}

enum E:
   case A(a:Int)
   case B
   case C

// @Modifier(rename="xx")
case class A(
  a: String,
  b: String,
)

enum Opt:
  case A(a:Int)
  case B
  case C


@main def test(): Unit = 
  // println(summon[OptionGiven[Annotation[Modifier,A]]].give)
  // println(summon[OptionGiven[Int]].give)
  // val jsonFile = Source.fromFile("openapi.json").getLines.mkString("\n")
  println(A("c","b").encode.serialize)
  println(Opt.B.encode)
  // println(Opt.B)