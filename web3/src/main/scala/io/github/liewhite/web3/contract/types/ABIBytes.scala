package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala
import io.github.liewhite.web3.contract.SizeValidator
import scala.compiletime.constValue


case class ABIStaticBytes[SIZE <: Int](value: Array[Byte], size: Int)

object ABIStaticBytes {
  inline given [SIZE <: Int]: ConvertFromScala[Array[Byte], ABIStaticBytes[SIZE]] =
    new ConvertFromScala[Array[Byte], ABIStaticBytes[SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(0),Some(32))
        size
      }
      def fromScala(s: Array[Byte]): Either[Exception,ABIStaticBytes[SIZE]] = Right(ABIStaticBytes(s, length))
    }
}

case class ABIDynamicBytes(value: Array[Byte])

object ABIDynamicBytes {
  inline given ConvertFromScala[Array[Byte], ABIDynamicBytes] =
    new ConvertFromScala[Array[Byte], ABIDynamicBytes] {
      def fromScala(s: Array[Byte]): Either[Exception,ABIDynamicBytes] = Right(ABIDynamicBytes(s))
    }
}

