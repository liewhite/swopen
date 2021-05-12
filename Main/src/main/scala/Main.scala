import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.schema.JsonSchema
import swopen.jsonToolbox.codec.Encoder
import swopen.openapi.v3_0_3.AdditionalProperties

case class A(a:Int|String,b:String)
case class B(a:Boolean|Int,b:Option[B])
case class C(c:Double,d:String|Boolean)
case class D(d1:Double,d2:String,d3:Boolean)

enum O:
  case A(a:Int)
  case B(b:String)
  
@main def test(): Unit =
  println(B(3,None).encode())