import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.*

import scala.deriving.*
import io.getquill.*

enum E derives Encoder,Decoder:
  case A(i: Int | String)
  case B(e: Vector[E] | Option[E])
  case C

enum Reps derives Encoder,Decoder:
  case A(a:Int)

@main def test(): Unit =
  println(E.A(1).encode.decode[E])
  // println(Reps.Int.encode.decode[Reps])

