package io.github.liewhite.web3.contract

import io.github.liewhite.web3.types.Address
import io.github.liewhite.json.codec.Encoder

// 必须要在调用处存在 两者类型的 typeclass才允许调用
trait ConvertFromScala[S, A] {
  def fromScala(s: S): A
  // 不需要转到scala, 因为Function返回的就是ABIValue
}

object ConvertFromScala {


  given ConvertFromScala[EmptyTuple, EmptyTuple] with {
    def fromScala(t: EmptyTuple) = t
  }

  given [H, T <: Tuple, ABIH,ABIT <: Tuple](using
      headConverter: => ConvertFromScala[H,ABIH],
      tailConverter: => ConvertFromScala[T, ABIT]
  ): ConvertFromScala[H *: T, ABIH *: ABIT] with {
    def fromScala(value: H *: T) = {
      value match {
        case (h *: t) => {
          headConverter.fromScala(h) *: tailConverter.fromScala(t)
        }
      }
    }
  }
}
