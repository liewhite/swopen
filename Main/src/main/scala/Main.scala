import scala.deriving.*

import com.liewhite.json.codec.*
import com.liewhite.json.JsonBehavior.*
import com.liewhite.json.annotations.Flat
import com.liewhite.json.typeclass.*

case class C(
  c: Option[A]
)

case class B(
  b: Int,
  c: C
)

enum D{
  case D1(c: Option[C])
  case D2
}
case class A(
  a:Int,

  @Flat
  bb: B,

  @Flat
  d: D
)

@main def test(): Unit =
  // 因为EB没有 repeatable annotation， 所以就被归类为coproduct， 导致encode函数无限递归
  val a = A(1, B(1,C(None)), D.D1(Some(C(None))))
  println(a.encode)
  // val m = summon[Mirror.ProductOf[E.EA]]
  // println(m)

