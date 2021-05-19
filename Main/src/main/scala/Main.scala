import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.Encoder
// import swopen.jsonToolbox.json.Json
// import swopen.jsonToolbox.schema.JsonSchema
// import swopen.jsonToolbox.codec.Encoder
import swopen.openapi.v3_0_3.Schema
import scala.deriving.*
import scala.compiletime.summonInline
import swopen.openapi.v3_0_3.*

case class Pdt(i:Int,b: Boolean|Option[Pdt])
enum E:
  case A(i:Int|String)
  case B(e: Vector[E] | Option[E])
  case C


// enum XEnum:
//   case B(b:Boolean)
//   case Scm(scm: FullX)

// case class X(
//   title: Option[String] = None,
//   `enum`: Option[Vector[Json]] = None,
//   const: Option[Json] = None,
//   `type`: Option[SchemaType] = None,
//   oneOf: Option[Vector[FullX]] = None,
//   items: Option[FullX] = None,
//   properties: Option[WithExtensions[Map[String, FullX]]] = None,
//   additionalProperties: Option[XEnum] = None,
// )

// type FullX = OrRef[WithExtensions[X]]

// def f[T](data:T)(using encoder: Encoder[T]) = encoder.encode(data)

def encode[T](data:T)(using e: => Encoder[T]) = e.encode(data)
@main def test(): Unit =
  println(Pdt(1,true).encode.decode[Pdt])
  // println(E.C.encode.decode[E])
  // println(E.B(Vector(E.C)).encode.decode[E])
  // println(SchemaType.array.encode)
  val d = OrRef.Id(WithExtensions(Pdt(1, None)))
  println(d.encode)
  val data = OrRef.Id(WithExtensions(SchemaInternal()))
  println(data.encode)
  // println(summon[Encoder[Schema]].encode(schema[E]))
  // val encoder = summon[Encoder[Schema]]
  // println(encoder.encode(schema[E]))
  // println(E.C.encode)
  // println(summon[Encoder[SchemaType]])
  // println(summon[Encoder[E]])
  // println(summon[Show[E]].show(E.B(None)))
  // println(summon[Mirror.SumOf[E.B.type]])
  // println(summon[Mirror.ProductOf[E.B.type]])
  // println(summon[XX[E.B.type]].f)
  // println(B(1, Some(B(false,None))).encode)
  // println(schema[B])
