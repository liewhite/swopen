package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala


case class ABIValueMap[K, V](value: Map[K, V])

object ABIValueMap {
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
}