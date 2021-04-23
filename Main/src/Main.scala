import swopen.jsonToolbox.schema.JsonSchema

import shapeless3.deriving.*
import swopen.jsonToolbox.constraint.{Constraint,NumberConstraint}
import swopen.jsonToolbox.json.{Json,JsonNumber}


case class A(
   // @Constraint(Vector(Json.JNull,Json.JNumber(JsonNumber.JInt(1))))
   @NumberConstraint(Vector(Json.JNull,Json.JNumber(JsonNumber.JInt(2))))
   a: Int,
   b: String,
   ) derives JsonSchema

@main def test(): Unit = 
   println(JsonSchema.schema[A])
   // println(Json.deserialize("""{"a":[1231,2,"a"]} """).toOption.get.serialize)
   // println(JsonBehavior.schema[B])
   // println(A(1).encode)
   
