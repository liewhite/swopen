package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala
import scala.annotation.tailrec

case class ABIMap[K, V](value: Map[K, V])

object ABIMap {
  given [K1, K2, V1, V2](using
      keyConvert: ConvertFromScala[K1, K2],
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Map[K1, V1], ABIMap[K2, V2]] =
    new ConvertFromScala[Map[K1, V1], ABIMap[K2, V2]] {

      def fromScala(s: Map[K1, V1]): Either[Exception, ABIMap[K2, V2]] = {
        val k = ConvertFromScala.convertSeq[K1, K2](s.keys.toVector)
        k match {
            case Right(keys) => {
                val v = ConvertFromScala.convertSeq[V1, V2](s.values.toVector)
                v match {
                    case Right(values) => Right(ABIMap(keys.zip(values).toMap))
                    case Left(e) => Left(e)
                }
            }
            case Left(e) => Left(e)
        }
      }
    }
}
