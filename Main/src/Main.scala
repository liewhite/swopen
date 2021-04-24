import swopen.jsonToolbox.JsonBehavior.encode
import swopen.jsonToolbox.JsonBehavior.decode
import swopen.jsonToolbox.json.{Json,JsonNumber}


case class A(
   a: Int,
   b: String,
)

enum E:
   case A(a:Int)
   case B
   case C


@main def test(): Unit = 
   // println(E.A(1).encode.serialize)
   // println(E.C.encode.serialize)
   // println(E.A(1).ordinal)
   val a = E.A(1)
   val b = E.B
   val c = E.C
   println(c.encode.decode[E])
   // println(Json.JObject(Map("a" -> Json.JNumber(JsonNumber.JInt(1)))).decode[E])
   
