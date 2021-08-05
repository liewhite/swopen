import scala.quoted.Expr

import com.liewhite.json.codec.*
import com.liewhite.json.JsonBehavior.*

case class A(a:Int, b:String = "b") derives Encoder,Decoder

@main def test(): Unit =
  println(A(1,"asd1").encode.decode[A])

