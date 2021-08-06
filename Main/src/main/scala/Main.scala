import scala.deriving.*

import io.github.liewhite.json.codec.*
import io.github.liewhite.json.JsonBehavior.*
import io.github.liewhite.json.annotations.Flat
import io.github.liewhite.json.typeclass.*

case class C(
  c: Option[A]
) derives Encoder,Decoder

case class B(
  b: Int,
) derives Encoder,Decoder

enum D  derives Encoder,Decoder{
  case D1(c: Option[C])
  case D2
}
case class A(
  a:Int,

  @Flat
  bb: B,

  @Flat
  d: D
) derives Encoder,Decoder

@main def test(): Unit =
  // 因为EB没有 repeatable annotation， 所以就被归类为coproduct， 导致encode函数无限递归
  val a = A(2111, B(1), D.D1(Some(C(None))))
  val b = A(33, B(1), D.D2)
  println(a.encode.decode[A])
  println(b.encode.decode[A])
  // val m = summon[Mirror.ProductOf[E.EA]]
  // println(m)

