import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.schema.JsonSchema
import swopen.openapi.v3_0_3.AdditionalProperties

case class A(a:Int|String,b:String)
case class B(a:Boolean,b:Option[B])
case class C(c:Double,d:String|Boolean)

@main def test(): Unit =
  // println(summon[JsonSchema[B]].schema.encode().serialize)
  // val b = AdditionalProperties.B(true)
  println(B(true, Some(B(true, None))).encode().serialize)