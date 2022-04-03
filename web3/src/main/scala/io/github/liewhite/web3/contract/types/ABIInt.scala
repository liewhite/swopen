package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.Extensions.*
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.SizeValidator
import io.github.liewhite.web3.contract.ABIPack


case class ABIInt(value: BigInt) {
  def toBytes: Array[Byte] = {
    value.toByteArray
  }
}

object ABIInt {
  inline given IntConverter: ConvertFromScala[Int, ABIInt] =
    new ConvertFromScala[Int, ABIInt] {
      def length: Int = 256
      def fromScala(s: Int): Either[Exception, ABIInt] = Right(
        ABIInt(s)
      )
    }

  inline given ConvertFromScala[BigInt, ABIInt] =
    new ConvertFromScala[BigInt, ABIInt] {
      def length: Int = 256
      def fromScala(s: BigInt): Either[Exception, ABIInt] = Right(
        ABIInt(s)
      )
    }

  inline given ABIPack[ABIInt] =
    new ABIPack[ABIInt] {
      def staticSize: Int = 32
      def typeName: String = s"int"
      def dynamic: Boolean = false
      def pack(i: ABIInt): Array[Byte] =
        i.value.toIntByte32

      def unpack(bytes: Array[Byte]): Either[Exception, ABIInt] = {
        val i = bytes.toBigInt
        i match {
          case Some(o) => 
            Right(ABIInt(o))
          case None => Left(Exception("failed parse int"))
        }
      }
    }
}

case class ABIUint(value: BigInt){
  // remove leading 0
  def toBytes: Array[Byte] = {
    value.toByteArray.dropWhile(_ == 0)
  }
}

object ABIUint {
  inline given  UintConverter:ConvertFromScala[Int, ABIUint] =
    new ConvertFromScala[Int, ABIUint] {
      def staticSize: Int = 32
      def fromScala(s: Int): Either[Exception, ABIUint] = Right(
        ABIUint(s)
      )
    }
  inline given ConvertFromScala[BigInt, ABIUint] =
    new ConvertFromScala[BigInt, ABIUint] {
      def length: Int = 256
      def fromScala(s: BigInt): Either[Exception, ABIUint] = Right(
        ABIUint(s)
      )
    }

  inline given ABIPack[ABIUint] =
    new ABIPack[ABIUint] {
      def staticSize: Int = 32
      def typeName: String = s"uint"
      def dynamic: Boolean = false

      def pack(i: ABIUint): Array[Byte] =
        if(i.value < 0 ) {
          throw Exception("negative uint: " + i.value)
        }
        i.value.toUintByte32

      def unpack(bytes: Array[Byte]): Either[Exception, ABIUint] = {
        val i = bytes.toBigUint
        i match {
          case Some(o) => 
            Right(ABIUint(o))
          case None => Left(Exception("failed parse uint"))
        }
      }
    }
}
