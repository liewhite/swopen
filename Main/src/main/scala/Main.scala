import scala.deriving.*

import com.liewhite.json.codec.*
import com.liewhite.json.JsonBehavior.*
import com.liewhite.json.annotations.Flat
import com.liewhite.json.typeclass.*

case class A(
  @Flat 
  a:Int,

  @Flat
  b:String | Boolean = "b")

enum E{
  case EA(a:Int)
  case EB
}

@main def test(): Unit =
  // 因为EB没有 repeatable annotation， 所以就被归类为coproduct， 导致encode函数无限递归
  val s = summon[Encoder[E.EB.type]]
  println(s)
  // val m = summon[Mirror.ProductOf[E.EA]]
  // println(m)

