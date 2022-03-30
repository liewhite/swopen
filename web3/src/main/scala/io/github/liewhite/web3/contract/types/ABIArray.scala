package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.ABIPack
import io.github.liewhite.web3.contract.SizeValidator
import io.github.liewhite.web3.common.unliftEither
import scala.annotation.tailrec

case class ABIStaticArray[T, SIZE <: Int](value: Vector[T], length: Int)

object ABIStaticArray {
  inline given [V1, V2, SIZE <: Int](using
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Seq[V1], ABIStaticArray[V2, SIZE]] =
    new ConvertFromScala[Seq[V1], ABIStaticArray[V2, SIZE]] {

      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, Some(1), None, None)
        size
      }

      def fromScala(s: Seq[V1]): Either[Exception, ABIStaticArray[V2, SIZE]] = {
        val result = ConvertFromScala.convertSeq[V1,V2](s)
        result.map(ABIStaticArray(_, length))
      }
    }

  
  inline given [V, SIZE <: Int](using vpack: ABIPack[V]): ABIPack[ABIStaticArray[V,SIZE]] =
    new ABIPack[ABIStaticArray[V,SIZE]] {
      def count: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, None, None, None)
        size
      }

      def length: Int = count * vpack.length


      // depends on whether elem is dynamic
      def dynamic: Boolean = vpack.dynamic

      def pack(i: ABIStaticArray[V,SIZE]): Array[Byte] = {
        val elems = i.value.map(vpack.pack(_))
        if(!dynamic) {
          elems.reduce(_ ++ _)
        }else{
          val staticSize = length * 32
          val dynamicStartOffset = staticSize
          // static acc, dynamic acc
          val result = elems.foldLeft((Array.emptyByteArray, Array.emptyByteArray, staticSize))((acc,item) => {
            (
              acc._1 ++ ABIPack.alignTo32(BigInt(acc._3).toByteArray,"left"),
              acc._2 ++ item,
              acc._3 + item.length
            )
          })
          result._1 ++ result._2
        }
      }

      def unpack(
          bytes: Array[Byte]
      ): Either[Exception, ABIStaticArray[V,SIZE]] = {
        val result = if(!dynamic) {
          Range(0,count).foldLeft(Vector.empty[Either[Exception, V]])((acc,item) => {
            val elemBytes = bytes.slice(vpack.length * item, vpack.length * (item +1))
            acc.appended(vpack.unpack(elemBytes))
          })
        }else{
          Range(0,count).foldLeft(Vector.empty[Either[Exception, V]])((acc,item) => {
            val elemOffset = BigInt(bytes.slice(vpack.length * item, vpack.length * (item +1))).toInt
            val elemBytes = bytes.slice(elemOffset, bytes.length)
            val elem = vpack.unpack(elemBytes)
            acc.appended(elem)
          })
        }
        unliftEither(result).map( item => ABIStaticArray[V,SIZE](item.toVector,count))
      }
    }
}

case class ABIDynamicArray[T](value: Vector[T]) {}

object ABIDynamicArray {
  given [V1, V2](using
      valueConvert: ConvertFromScala[V1, V2]
  ): ConvertFromScala[Seq[V1], ABIDynamicArray[V2]] =
    new ConvertFromScala[Seq[V1], ABIDynamicArray[V2]] {

      def fromScala(s: Seq[V1]): Either[Exception, ABIDynamicArray[V2]] = {
        val result = ConvertFromScala.convertSeq[V1,V2](s)
        result.map(ABIDynamicArray(_))
      }
    }
}