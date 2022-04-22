package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.types.BytesType
import io.github.liewhite.web3.contract.ABIPack

case class ABIString(value: String) extends ABIType

object ABIString {

    given ConvertFromScala[String, ABIString] with {
        def fromScala(s: String): ABIString = ABIString(s)
    }

    given ABIPack[ABIString] with {
        def dynamic: Boolean = true

        def staticSize: Int                 = 32
        def typeName: String                = s"string"
        def pack(a: ABIString): Array[Byte] = {
            val bytes       = a.value.getBytes
            val length      = bytes.length
            val lengthBytes = BigInt(length).toUintByte32
            val body        = padString(a.value)
            lengthBytes ++ body
        }

        def unpack(bytes: Array[Byte]): ABIString = {
            val bytesLen = bytes.length
            if (bytesLen < 32) {
                throw Exception(
                  "bad bytes length for string ,at least 32, got: " + bytesLen
                )
            } else {
                val lengthBytes = bytes.slice(0, 32)
                val length      = lengthBytes.toBigUint.intValue
                if (bytesLen < 32 + length) {
                    throw Exception(
                      s"bad bytes length for string ,at least 32 + ${length}, got: " + bytesLen
                    )
                } else {
                    ABIString(new String(bytes.slice(32, 32 + length)))
                }
            }
        }
    }
}
