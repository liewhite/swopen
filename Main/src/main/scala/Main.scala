import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.Encoder

import scala.deriving.*

enum E:
  case A(i:Int|String)
  case B(e: Vector[E] | Option[E])
  case C

case class X(x: Int)

@main def test(): Unit = 
  println(E.A("as").encode.toPrettyString)
  // println(FullSchema(SchemaInternal()).encode)
  // println(E.C.encode)
  // println(schema[p1.p11.E].encode.serialize(pretty = true))
