import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.Encoder

import scala.deriving.*
import io.getquill.*

enum E:
  case A(i:Int|String)
  case B(e: Vector[E] | Option[E])
  case C

@main def test(): Unit = 
  summon[Mirror.ProductOf[E.C.type]]
  summon[Mirror.SumOf[E.C.type]]

