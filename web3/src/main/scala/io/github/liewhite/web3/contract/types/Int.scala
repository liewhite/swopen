package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.SizeValidator

case class ABIInt[SIZE <: Int](value: BigInt, size: Int)

object ABIInt {
  inline given [SIZE <: Int]: ConvertFromScala[Int, ABIInt[SIZE]] =
    new ConvertFromScala[Int, ABIInt[SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
        size
      }
      def fromScala(s: Int): Either[Exception,ABIInt[SIZE]] = Right(ABIInt(s, length))
    }
}

case class ABIUint[SIZE <: Int](value: BigInt, size: Int)

object ABIUint {
  inline given [SIZE <: Int]: ConvertFromScala[Int, ABIUint[SIZE]] =
    new ConvertFromScala[Int, ABIUint[SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
        size
      }
      def fromScala(s: Int): Either[Exception,ABIUint[SIZE]] = Right(ABIUint(s, length))
    }
}
