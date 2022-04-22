package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.contract.SizeValidator
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.ABIPack

case class ABIStaticBytes[SIZE <: Int](value: Array[Byte], size: Int) extends ABIType

object ABIStaticBytes {
    inline given [SIZE <: Int]: ConvertFromScala[Array[Byte], ABIStaticBytes[SIZE]] =
        new ConvertFromScala[Array[Byte], ABIStaticBytes[SIZE]] {
            def length: Int                                     = {
                inline val size: Int = constValue[SIZE]
                SizeValidator.validateSize(size, Some(0), Some(32))
                size
            }
            def fromScala(s: Array[Byte]): ABIStaticBytes[SIZE] = {
                ABIStaticBytes(s, length)
            }
        }

    inline given [SIZE <: Int]: ABIPack[ABIStaticBytes[SIZE]] =
        new ABIPack[ABIStaticBytes[SIZE]] {

            // 0<=size<=32
            def staticSize: Int = 32

            def length: Int = {
                inline val size: Int = constValue[SIZE]
                SizeValidator.validateSize(size, None, None, None)
                size
            }

            def typeName: String = s"byte[${length}]"

            def dynamic: Boolean = false

            def pack(i: ABIStaticBytes[SIZE]): Array[Byte] = {
                padBytes(i.value)
            }

            def unpack(
                bytes: Array[Byte]
            ): ABIStaticBytes[SIZE] = {
                if (bytes.length < length) {
                    throw Exception(
                      s"bytes length not enough, expect ${length}, got ${bytes.length}"
                    )

                } else {
                    ABIStaticBytes[SIZE](bytes.slice(0, length), length)
                }
            }
        }
}

case class ABIDynamicBytes(value: Array[Byte]) extends ABIType

object ABIDynamicBytes {
    inline given ConvertFromScala[Array[Byte], ABIDynamicBytes] = {
        new ConvertFromScala[Array[Byte], ABIDynamicBytes] {
            def fromScala(s: Array[Byte]): ABIDynamicBytes = ABIDynamicBytes(s)
        }
    }

    inline given ABIPack[ABIDynamicBytes] = {
        new ABIPack[ABIDynamicBytes] {
            def staticSize: Int  = 32
            def typeName: String = s"bytes"
            def dynamic: Boolean = true

            def pack(i: ABIDynamicBytes): Array[Byte] = {
                val lengthBytes = BigInt(i.value.length).toUintByte32
                lengthBytes ++ padBytes(i.value)
            }

            def unpack(
                bytes: Array[Byte]
            ): ABIDynamicBytes = {
                val bytesLen = bytes.length
                if (bytesLen < 32) {
                    throw Exception(
                      "bad bytes length for byte[] ,at least 32, got: " + bytesLen
                    )

                } else {
                    val lengthBytes = bytes.slice(0, 32)
                    val length      = lengthBytes.toBigUint.toInt
                    if (bytesLen < 32 + length) {
                        throw Exception(
                          s"bad bytes length for byte[] ,at least 32 + ${length}, got: " + bytesLen
                        )
                    } else {
                        ABIDynamicBytes(bytes.slice(32, 32 + length))
                    }
                }
            }
        }

    }
}
