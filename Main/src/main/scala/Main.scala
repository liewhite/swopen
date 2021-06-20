import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.Encoder

import scala.deriving.*
import io.getquill.*

enum E:
  case A(i: Int | String)
  case B(e: Vector[E] | Option[E])
  case C

enum Reps:
  case Int

@main def test(): Unit =
  1
