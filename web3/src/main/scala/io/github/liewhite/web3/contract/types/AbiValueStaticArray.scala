package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala

// static array
case class ABIValueStaticArray[T](value: Vector[T], size: Int)

object ABIValueStaticArray{
  given [V1, V2](using
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Seq[V1], ABIValueStaticArray[V2]] with {
    def fromScala(s: Seq[V1]): ABIValueStaticArray[V2] = {
      ABIValueStaticArray(s.map(valueConvert.fromScala(_)).toVector, s.length)
    }
  }
}