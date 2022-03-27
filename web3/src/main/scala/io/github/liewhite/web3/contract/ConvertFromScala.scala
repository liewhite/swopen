package io.github.liewhite.web3.contract

import io.github.liewhite.web3.types.Address
import io.github.liewhite.json.codec.Encoder

// 如果先把scala转为统一表示， 后续容易处理, 但是这样会丢失编译时检查
// 必须要在调用处存在 两者类型的 typeclass才允许调用
trait ConvertFromScala[S, A] {
  def fromScala(s: S): A
  // 不需要转到scala, 因为Function返回的就是ABIValue
}

object ConvertFromScala {
  given [K1, K2, V1, V2](using
      keyConvert: ConvertFromScala[K1, K2],
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Map[K1, V1], ABIValueMap[K2, V2]] with {
    def fromScala(s: Map[K1, V1]): ABIValueMap[K2, V2] = {
      val abiValue = s.map { case (k, v) =>
        (keyConvert.fromScala(k), valueConvert.fromScala(v))
      }
      ABIValueMap[K2, V2](abiValue)
    }
  }
  given [V1, V2](using
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Seq[V1], ABIValueDynamicArray[V2]] with {
    def fromScala(s: Seq[V1]): ABIValueDynamicArray[V2] = {
      ABIValueDynamicArray(s.map(valueConvert.fromScala(_)).toVector)
    }
  }

  given [V1, V2](using
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Seq[V1], ABIValueStaticArray[V2]] with {
    def fromScala(s: Seq[V1]): ABIValueStaticArray[V2] = {
      ABIValueStaticArray(s.map(valueConvert.fromScala(_)).toVector, s.length)
    }
  }

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
