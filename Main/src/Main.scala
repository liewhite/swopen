import swopen.jsonToolbox.JsonBehavior
import swopen.jsonToolbox.ItemEnum
import swopen.jsonToolbox.json.Json
// import swopen.jsonToolbox.utils.given

import scala.jdk.CollectionConverters.*

import shapeless3.deriving.*


case class A(a: Int) extends scala.annotation.Annotation
case class B(
   a: Int,

   b: Opt[Int]
) 

enum Opt[+T]{
   case Sm(t: T)
   case NnE
}

@main def test(): Unit = 
   // val anns = Annotations[ItemEnum, B].apply()
   // println(anns(0).get.values.asJson)
   // println(JsonBehavior.schema[Opt.Sm[Int]])
   println(Json.parseJson(""" {"a": [1,2,"a"]} """))
   println(JsonBehavior.schema[B])
