import swopen.jsonToolbox.JsonBehavior.encode
import swopen.jsonToolbox.JsonBehavior.decode
import swopen.jsonToolbox.json.{Json,JsonNumber}
import scala.io.Source
import scala.math.BigInt

enum E:
   case A(a:Int)
   case B
   case C

case class A(
   a: BigInt
)



@main def test(): Unit = 
   // val jsonFile = Source.fromFile("openapi.json").getLines.mkString("\n")
   println(Json.deserialize("""{"a":1111111111111111111111111111111111111111111111111111111111111}""").toOption.get.decode[A])
   // println(Json.JObject(Map("a" -> Json.JNumber(JsonNumber.JInt(1)))).decode[E])
   
