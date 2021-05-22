import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.Encoder
import swopen.openapi.v3_0_3.*

import scala.deriving.*

enum E:
  case A(i:Int|String)
  case B(e: Vector[E] | Option[E])
  case C

case class X(x: Int)

@main def test(): Unit =
  println(FullSchema(SchemaInternal()).encode)
  // println(summon[Encoder[E]])
