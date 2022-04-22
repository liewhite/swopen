package io.github.liewhite.web3.common

import scala.annotation.tailrec
import scala.reflect.ClassTag

// 必须要在调用处存在 两者类型的 typeclass才允许调用
trait ConvertFromScala[-S, +A] {
  def fromScala(s: S): A
}
object ConvertFromScala {
  given UnitConverter: ConvertFromScala[Unit, Unit] with {
    def fromScala(t: Unit) = t
  }

  given EmptyTupleConverter: ConvertFromScala[EmptyTuple, EmptyTuple] with {
    def fromScala(t: EmptyTuple) = t
  }

  given TupleConverter[H, T <: Tuple, ABIH, ABIT <: Tuple](using
      headConverter: ConvertFromScala[H, ABIH],
      tailConverter: ConvertFromScala[T, ABIT]
  ): ConvertFromScala[H *: T, ABIH *: ABIT] = {
    new ConvertFromScala[H *: T, ABIH *: ABIT] {
      def fromScala(value: H *: T) = {
        value match {
          case (h *: t) => {
            val hResult = headConverter.fromScala(h) *: tailConverter.fromScala(t)
            hResult
          }
        }
      }
    }
  }
}
