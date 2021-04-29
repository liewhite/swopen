import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.schema.DefaultValue

case class A(c:Option[List[String]],nodefalt:Int,a:Int = 1,b:Vector[Map[String,Int]] = Vector(Map("asd" -> 123)))

enum E:
  case A(a:Int)
  case B

def default[A](using a:DefaultValue[A]) = a

@main def test(): Unit = 
  // val a = println(A(None, 2).encode().serialize)
  println(Json.deserialize(E.A(1).encode().serialize).toOption.get.decode[E].toOption.get)
