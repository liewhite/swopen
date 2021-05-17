import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.Encoder
// import swopen.jsonToolbox.json.Json
// import swopen.jsonToolbox.schema.JsonSchema
// import swopen.jsonToolbox.codec.Encoder
import swopen.openapi.v3_0_3.Schema
import shapeless3.deriving.*
import scala.deriving.*
import scala.compiletime.summonInline
import swopen.openapi.v3_0_3.*

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

@main def test(): Unit =
  // println(summon[Mirror.SumOf[E.C.type]])
  // println(E.B(Vector(E.C)).encode.decode[E])
  // println(summon[Encoder[SchemaType]])
  // println(summon[Encoder[Schema]])
  // println(summon[Encoder[Schema]].encode(schema[E]))
  println(schema[E].encode)
  // println(summon[Encoder[SchemaType]])
  // println(summon[Encoder[E]])
  // println(summon[Show[E]].show(E.B(None)))
  // println(summon[Mirror.SumOf[E.B.type]])
  // println(summon[Mirror.ProductOf[E.B.type]])
  // println(summon[XX[E.B.type]].f)
  // println(B(1, Some(B(false,None))).encode)
  // println(schema[B])
