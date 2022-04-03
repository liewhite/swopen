package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.Extensions.*
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.ABIPack
import io.github.liewhite.web3.contract.SizeValidator
import io.github.liewhite.web3.common.unliftEither
import scala.annotation.tailrec

case class ABIStaticArray[T, SIZE <: Int](value: Vector[T], length: Int) {}

object ABIStaticArray {
  inline given StaticArrayConverter[V1, V2, SIZE <: Int](using
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Seq[V1], ABIStaticArray[V2, SIZE]] = {
    new ConvertFromScala[Seq[V1], ABIStaticArray[V2, SIZE]] {
      // def length: Int = 2
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(0), None, None)
        size
      }

      def fromScala(s: Seq[V1]): Either[Exception, ABIStaticArray[V2, SIZE]] = {
        val result = ConvertFromScala.convertSeq[V1, V2](s)
        result.map(ABIStaticArray(_, length))
      }
    }
  }

  inline given [V, SIZE <: Int](using
      vpack: ABIPack[V]
  ): ABIPack[ABIStaticArray[V, SIZE]] =
    new ABIPack[ABIStaticArray[V, SIZE]] {
      def count: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, None, None, None)
        size
      }

      def staticSize: Int = count * vpack.staticSize

      def typeName: String = s"${vpack.typeName}[${count}]"

      // depends on whether elem is dynamic
      def dynamic: Boolean = vpack.dynamic

      def pack(i: ABIStaticArray[V, SIZE]): Array[Byte] = {
        val elems = i.value.map(vpack.pack(_))
        if (!vpack.dynamic) {
          elems.reduce(_ ++ _)
        } else {
          val staticSize = count * 32
          // static acc, dynamic acc
          val result = elems.foldLeft(
            (Array.emptyByteArray, Array.emptyByteArray, staticSize)
          )((acc, item) => {
            (
              acc._1 ++ BigInt(acc._3).toUintByte32.get,
              acc._2 ++ item,
              acc._3 + item.length
            )
          })
          result._1 ++ result._2
        }
      }

      def unpack(
          bytes: Array[Byte]
      ): Either[Exception, ABIStaticArray[V, SIZE]] = {
        if (bytes.length < vpack.staticSize * count) {
          return Left(Exception(
            "failed unpack array, exceed bytes boundry:" + vpack.staticSize * count
          ))
        }
        val result = if (!vpack.dynamic) {
          Range(0, count).foldLeft(Vector.empty[Either[Exception, V]])(
            (acc, item) => {
              val elemBytes =
                bytes.slice(
                  vpack.staticSize * item,
                  vpack.staticSize * (item + 1)
                )
              acc.appended(vpack.unpack(elemBytes))
            }
          )
        } else {
          Range(0, count).foldLeft(Vector.empty[Either[Exception, V]])(
            (acc, item) => {
              val elemOffset = (
                bytes
                  .slice(vpack.staticSize * item, vpack.staticSize * (item + 1))
                )
                .toBigUint
              if (!elemOffset.isDefined) {
                acc.appended(Left(Exception("failed parse elemt:")))
              } else {
                val elemBytes = bytes.slice(elemOffset.get.toInt, bytes.length)
                val elem = vpack.unpack(elemBytes)
                acc.appended(elem)
              }
            }
          )
        }
        unliftEither(result).map(item =>
          ABIStaticArray[V, SIZE](item.toVector, count)
        )
      }
    }
}

case class ABIDynamicArray[T](value: Vector[T]) {}

object ABIDynamicArray {
  given DynamicArrayConverter[V1, V2](using
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Seq[V1], ABIDynamicArray[V2]] =
    new ConvertFromScala[Seq[V1], ABIDynamicArray[V2]] {

      def fromScala(s: Seq[V1]): Either[Exception, ABIDynamicArray[V2]] = {
        val result = ConvertFromScala.convertSeq[V1, V2](s)
        result.map(ABIDynamicArray(_))
      }
    }

  given [V](using vpack: ABIPack[V]): ABIPack[ABIDynamicArray[V]] =
    new ABIPack[ABIDynamicArray[V]] {
      def staticSize: Int = 32
      def typeName: String = s"${vpack.typeName}[]"

      // depends on whether elem is dynamic
      def dynamic: Boolean = true

      def pack(i: ABIDynamicArray[V]): Array[Byte] = {
        val count = i.value.length

        val countBytes = BigInt(count).toUintByte32.get
        val elemDynamic = vpack.dynamic

        val elems = i.value.map(vpack.pack(_))
        // pack count
        val body = if (!elemDynamic) {
          elems.reduce(_ ++ _)
        } else {
          val bodySize = count * 32
          val dynamicStartOffset = bodySize
          // static acc, dynamic acc
          val result = elems.foldLeft(
            (Array.emptyByteArray, Array.emptyByteArray, bodySize)
          )((acc, item) => {
            (
              acc._1 ++ BigInt(acc._3).toUintByte32.get,
              acc._2 ++ item,
              acc._3 + item.length
            )
          })
          result._1 ++ result._2
        }
        countBytes ++ body
      }

      def unpack(
          bytes: Array[Byte]
      ): Either[Exception, ABIDynamicArray[V]] = {
        if (bytes.length < 32) {
          return Left(
            Exception(
              s"not enough bytes to unpack ${vpack.typeName}[], len: ${bytes.length}"
            )
          )
        }
        val countOption = (bytes.slice(0, 32)).toBigUint
        if (!countOption.isDefined) {
          return Left(
            Exception(
              s"not enough bytes to unpack ${vpack.typeName}[], len: ${bytes.length}"
            )
          )
        }
        val count = countOption.get.toInt
        val elemDynamic = vpack.dynamic

        val bodyBytes = bytes.slice(32, bytes.length)

        if (bodyBytes.length < vpack.staticSize * count) {
          return Left(Exception(
            "failed unpack array, exceed bytes boundry:" + vpack.staticSize * count
          ))
        }
        val result = if (!elemDynamic) {
          Range(0, count).foldLeft(Vector.empty[Either[Exception, V]])(
            (acc, item) => {
              val elemBytes =
                bodyBytes.slice(
                  vpack.staticSize * item,
                  vpack.staticSize * (item + 1)
                )
              acc.appended(vpack.unpack(elemBytes))
            }
          )
        } else {
          Range(0, count).foldLeft(Vector.empty[Either[Exception, V]])(
            (acc, item) => {
              val elemOffset = (
                bodyBytes
                  .slice(
                    vpack.staticSize * item,
                    vpack.staticSize * (item + 1)
                  )
                )
                .toBigUint
              if (!elemOffset.isDefined) {
                acc.appended(Left(Exception("failed parse elem offset")))
              } else {
                val elemBytes = bodyBytes.slice(elemOffset.get.toInt, bodyBytes.length)
                val elem = vpack.unpack(elemBytes)
                acc.appended(elem)

              }
            }
          )
        }
        unliftEither(result).map(item => ABIDynamicArray[V](item.toVector))
      }
    }
}
