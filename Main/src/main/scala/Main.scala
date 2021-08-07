import scala.deriving.*

import io.github.liewhite.json.codec.*
import io.github.liewhite.json.JsonBehavior.*
import io.github.liewhite.json.annotations.Flat
import io.github.liewhite.json.typeclass.*

enum D  derives Encoder,Decoder{
  case D1(c: Int)
  case D2
}

case class F(j:Boolean,k: String)  derives Encoder,Decoder
case class A(i:Int, @Flat f: F) derives Encoder,Decoder

@main def test(): Unit =
  println(A(1,F(true,"flatten")).encode.noSpaces)
  assert(A(1,F(true,"flatten")).encode.noSpaces == """{"i":1,"j":true,"k":"flatten"}""")
  assert(D.D2.encode.noSpaces == """"D2"""")
  assert(D.D1(1).encode.noSpaces == """{"c":1}""")

