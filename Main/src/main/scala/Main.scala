import scala.deriving.*

import com.liewhite.json.codec.*
import com.liewhite.json.JsonBehavior.*
import com.liewhite.json.annotations.Flat
import com.liewhite.json.typeclass.*

case class B(
  b: Int
)
case class A(
  @Flat
  a:Int,

  @Flat
  bb: B
)

@main def test(): Unit =
  // 因为EB没有 repeatable annotation， 所以就被归类为coproduct， 导致encode函数无限递归
  val a = A(1, B(2))
  println(a.encode)
  // val m = summon[Mirror.ProductOf[E.EA]]
  // println(m)

