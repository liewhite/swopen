package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala

// dynamic array
case class ABIValueDynamicArray[T](value: Vector[T])
object ABIValueDynamicArray{
  given [V1, V2](using
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Seq[V1], ABIValueDynamicArray[V2]] with {
    def fromScala(s: Seq[V1]): ABIValueDynamicArray[V2] = {
      ABIValueDynamicArray(s.map(valueConvert.fromScala(_)).toVector)
    }
  }
}