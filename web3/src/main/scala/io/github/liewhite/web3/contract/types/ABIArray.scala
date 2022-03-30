package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.ABIPack
import io.github.liewhite.web3.contract.SizeValidator
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
        val result = convertSeq[V1,V2](s)
        result.map(ABIStaticArray(_, length))
      }
    }

  
  inline given [V, SIZE <: Int](using vpack: ABIPack[V]): ABIPack[ABIStaticArray[V,SIZE]] =
    new ABIPack[ABIStaticArray[V,SIZE]] {
      def length: Int = {
        inline val size: Int = constValue[SIZE]
        SizeValidator.validateSize(size, None, None, None)
        size
      }

      // depends on if elem is static
      def dynamic: Boolean = vpack.dynamic

      // if elems is static, just pack in place, otherwise 
      // pack elements and concat
      def pack(i: ABIStaticArray[V,SIZE]): Array[Byte] = {
        val elems = i.value.map(vpack.pack(_))
        // ABIPack.alignTo32(i.value,"right")
        ???
      }

      def unpack(
          bytes: Array[Byte]
      ): Either[Exception, ABIStaticArray[V,SIZE]] = {
        // if (bytes.length < length) {
        //   Left(
        //     Exception(
        //       s"bytes length not enough, expect ${length}, got ${bytes.length}"
        //     )
        //   )
        // } else {
        //   Right(ABIStaticBytes[SIZE](bytes.slice(0,length), length))
        // }
        ???
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
        val result = convertSeq[V1,V2](s)
        result.map(ABIDynamicArray(_))
      }
    }
}
