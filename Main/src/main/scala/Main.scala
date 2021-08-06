import scala.deriving.*

import io.github.liewhite.json.codec.*
import io.github.liewhite.json.JsonBehavior.*
import io.github.liewhite.json.annotations.Flat
import io.github.liewhite.json.typeclass.*

case class C(
  c: Option[A]
)

case class B(
  b: Int,
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
  val a = A(1, B(1), D.D1(Some(C(None))))
  val b = A(1, B(1), D.D2)
  println(a.encode.decode[A])
  println(b.encode.decode[A])
  // val m = summon[Mirror.ProductOf[E.EA]]
  // println(m)

