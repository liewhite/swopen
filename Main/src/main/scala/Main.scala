import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.schema.DefaultValue
import swopen.jsonToolbox.schema.QualifiedName
import scala.deriving.*


case class A(a:Int|String,b:String)
case class B(a:Boolean,b:Double)
@main def test(): Unit =
  val a : A | B = A(1,"asd")
  val b : A | B = B(true,1.2)
  println(a.encode().decode[A|B])

