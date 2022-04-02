package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.common.*
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.SizeValidator
import io.github.liewhite.web3.contract.ABIPack


case class ABIInt(value: BigInt)

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
        padInt(i.value)

      def unpack(bytes: Array[Byte]): Either[Exception, ABIInt] = {
        val i = BigInt(bytes)
        Right(ABIInt(i))
      }
    }
}

case class ABIUint(value: BigInt)

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
        padUint(i.value)

      def unpack(bytes: Array[Byte]): Either[Exception, ABIUint] = {
        val i = BigInt(bytes)
        Right(ABIUint(i))
      }
    }
}
