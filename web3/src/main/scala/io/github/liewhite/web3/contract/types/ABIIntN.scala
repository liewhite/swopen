package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.Extensions.*
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.SizeValidator
import io.github.liewhite.web3.contract.ABIPack

case class ABIIntN[SIZE <: Int](value: BigInt, size: Int){
  def toBytes: Array[Byte] = {
    value.toByteArray
  }
}

object ABIIntN {
  inline given IntNConverter[SIZE <: Int]
      : ConvertFromScala[Int, ABIIntN[SIZE]] =
    new ConvertFromScala[Int, ABIIntN[SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(8), Some(256), Some(8))
        size
      }
      def fromScala(s: Int): Either[Exception, ABIIntN[SIZE]] = Right(
        ABIIntN(s, length)
      )
    }
  inline given [SIZE <: Int]: ConvertFromScala[BigInt, ABIIntN[SIZE]] = {
    new ConvertFromScala[BigInt, ABIIntN[SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(8), Some(256), Some(8))
        size
      }
      def fromScala(s: BigInt): Either[Exception, ABIIntN[SIZE]] = Right(
        ABIIntN(s, length)
      )
    }
  }
  inline given [SIZE <: Int]: ABIPack[ABIIntN[SIZE]] =
    new ABIPack[ABIIntN[SIZE]] {
      def staticSize: Int = 32
      def typeName: String = s"int${if (length == 0) "" else length}"
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(0), Some(256), Some(8))
        size
      }

      def dynamic: Boolean = false
      def pack(i: ABIIntN[SIZE]): Array[Byte] =
        i.value.toIntByte32

      def unpack(bytes: Array[Byte]): Either[Exception, ABIIntN[SIZE]] = {
        val i = bytes.toBigInt
        i match {
          case Some(o) => 
            Right(ABIIntN[SIZE](o, length))
          case None => Left(Exception("failed parse int"))
        }
      }
    }
}

case class ABIUintN[SIZE <: Int](value: BigInt, size: Int){
  // remove leading 0
  def toBytes: Array[Byte] = {
    value.toByteArray.dropWhile(_ == 0)
  }
}

object ABIUintN {
  inline given UintNConverter[SIZE <: Int]
      : ConvertFromScala[Int, ABIUintN[SIZE]] =
    new ConvertFromScala[Int, ABIUintN[SIZE]] {
      def staticSize: Int = 32
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
        size
      }
      def fromScala(s: Int): Either[Exception, ABIUintN[SIZE]] = Right(
        ABIUintN(s, length)
      )
    }
  inline given [SIZE <: Int]: ConvertFromScala[BigInt, ABIUintN[SIZE]] =
    new ConvertFromScala[BigInt, ABIUintN[SIZE]] {
      def staticSize: Int = 32
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
        size
      }
      def fromScala(s: BigInt): Either[Exception, ABIUintN[SIZE]] = Right(
        ABIUintN(s, length)
      )
    }

  inline given [SIZE <: Int]: ABIPack[ABIUintN[SIZE]] =
    new ABIPack[ABIUintN[SIZE]] {
      def staticSize: Int = 32

      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(0), Some(256), Some(8))
        size
      }
      def typeName: String = s"uint${if (length == 0) "" else length}"
      def dynamic: Boolean = false

      def pack(i: ABIUintN[SIZE]): Array[Byte] =
        if (i.value < 0) {
          throw Exception("negative uint: " + i.value)
        }
        i.value.toUintByte32

      def unpack(bytes: Array[Byte]): Either[Exception, ABIUintN[SIZE]] = {
        val i = bytes.toBigUint
        i match {
          case Some(o) => {
            if (o < 0) {
              Left(Exception("negative uint: " + i))
            } else {
              Right(ABIUintN[SIZE](o, length))
            }
          }
          case None => Left(Exception("failed parse uint"))
        }
      }
    }
}
