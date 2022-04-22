package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.common.ConvertFromScala
import scala.annotation.tailrec

case class ABIMap[K, V](value: Map[K, V]) extends ABIType

object ABIMap {
  given [K1, K2, V1, V2](using
      keyConvert: ConvertFromScala[K1, K2],
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Map[K1, V1], ABIMap[K2, V2]] =
    new ConvertFromScala[Map[K1, V1], ABIMap[K2, V2]] {

      def fromScala(s: Map[K1, V1]): ABIMap[K2, V2] = {
        val keys = s.keys.toVector.map(keyConvert.fromScala(_))
        val v = s.values.toVector.map(valueConvert.fromScala(_))
        ABIMap(keys.zip(v).toMap)
      }
    }
}
