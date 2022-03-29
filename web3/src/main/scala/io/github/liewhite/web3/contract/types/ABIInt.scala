package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.SizeValidator
import io.github.liewhite.web3.contract.ABIPack

case class ABIInt[SIZE <: Int](value: BigInt, size: Int)

object ABIInt {
  inline given [SIZE <: Int]: ConvertFromScala[Int, ABIInt[SIZE]] =
    new ConvertFromScala[Int, ABIInt[SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
        size
      }
      def fromScala(s: Int): Either[Exception, ABIInt[SIZE]] = Right(
        ABIInt(s, length)
      )
    }
  inline given [SIZE <: Int]: ABIPack[ABIInt[SIZE]] =
    new ABIPack[ABIInt[SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
        size
      }

      def dynamic: Boolean = false
      def pack(i: ABIInt[SIZE]): Array[Byte] =
        ABIPack.alignTo32(i.value.toByteArray, "left")

      def unpack(bytes: Array[Byte]): Either[Exception, ABIInt[SIZE]] = {
        val i = BigInt(bytes)
        Right(ABIInt[SIZE](i, length))
      }
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
      def fromScala(s: Int): Either[Exception, ABIUint[SIZE]] = Right(
        ABIUint(s, length)
      )
    }

  inline given [SIZE <: Int]: ABIPack[ABIUint[SIZE]] =
    new ABIPack[ABIUint[SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
        size
      }
      def dynamic: Boolean = false

      def pack(i: ABIUint[SIZE]): Array[Byte] =
        ABIPack.alignTo32(i.value.toByteArray, "left")

      def unpack(bytes: Array[Byte]): Either[Exception, ABIUint[SIZE]] = {
        val i = BigInt(bytes)
        Right(ABIUint[SIZE](i, length))
      }
    }
}
