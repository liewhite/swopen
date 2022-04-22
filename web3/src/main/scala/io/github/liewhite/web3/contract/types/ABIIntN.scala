package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.Extensions.*
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.SizeValidator
import io.github.liewhite.web3.contract.ABIPack

case class ABIIntN[SIZE <: Int](value: BigInt, size: Int) extends ABIType

object ABIIntN {
    inline given IntNConverter[SIZE <: Int]: ConvertFromScala[Int, ABIIntN[SIZE]] =
        new ConvertFromScala[Int, ABIIntN[SIZE]] {
            def length: Int                      = {
                inline val size: Int = constValue[SIZE]
                SizeValidator.validateSize(size, Some(8), Some(256), Some(8))
                size
            }
            def fromScala(s: Int): ABIIntN[SIZE] = ABIIntN(s, length)
        }

    inline given [SIZE <: Int]: ConvertFromScala[BigInt, ABIIntN[SIZE]] = {
        new ConvertFromScala[BigInt, ABIIntN[SIZE]] {

            def length: Int = {
                inline val size: Int = constValue[SIZE]
                SizeValidator.validateSize(size, Some(8), Some(256), Some(8))
                size
            }

            def fromScala(s: BigInt): ABIIntN[SIZE] = ABIIntN(s, length)
        }
    }

    inline given [SIZE <: Int]: ABIPack[ABIIntN[SIZE]] =
        new ABIPack[ABIIntN[SIZE]] {
            def staticSize: Int  = 32
            def typeName: String = s"int${if (length == 0) "" else length}"
            def length: Int      = {
                inline val size: Int = constValue[SIZE]
                SizeValidator.validateSize(size, Some(0), Some(256), Some(8))
                size
            }

            def dynamic: Boolean                    = false
            def pack(i: ABIIntN[SIZE]): Array[Byte] =
                i.value.toIntByte32

            def unpack(bytes: Array[Byte]): ABIIntN[SIZE] = {
                ABIIntN[SIZE](bytes.toBigInt, length)
            }
        }
}

case class ABIUintN[SIZE <: Int](value: BigInt, size: Int) extends ABIType

object ABIUintN {
    inline given UintNConverter[SIZE <: Int]: ConvertFromScala[Int, ABIUintN[SIZE]] =
        new ConvertFromScala[Int, ABIUintN[SIZE]] {
            def staticSize: Int                   = 32
            def length: Int                       = {
                inline val size: Int = constValue[SIZE]
                SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
                size
            }
            def fromScala(s: Int): ABIUintN[SIZE] = ABIUintN(s, length)
        }

    inline given [SIZE <: Int]: ConvertFromScala[BigInt, ABIUintN[SIZE]] =
        new ConvertFromScala[BigInt, ABIUintN[SIZE]] {
            def staticSize: Int = 32

            def length: Int = {
                inline val size: Int = constValue[SIZE]
                SizeValidator.validateSize(size, Some(1), Some(256), Some(8))
                size
            }

            def fromScala(s: BigInt): ABIUintN[SIZE] = ABIUintN(s, length)
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

            def unpack(bytes: Array[Byte]): ABIUintN[SIZE] = {
                val i = bytes.toBigUint
                if (i < 0) {
                    throw java.lang.IllegalArgumentException("negative uint: " + i)
                }
                ABIUintN[SIZE](i, length)
            }
        }
}
