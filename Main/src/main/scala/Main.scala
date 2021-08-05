import scala.quoted.Expr

import com.liewhite.json.codec.*
import com.liewhite.json.JsonBehavior.*

case class A(a:Int, b:String | Boolean = "b") derives Encoder,Decoder

@main def test(): Unit =
  println(A(1,true).encode.decode[A])

