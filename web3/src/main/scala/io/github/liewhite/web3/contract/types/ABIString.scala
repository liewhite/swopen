package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.ConvertFromScala
import io.github.liewhite.web3.types.BytesType
import io.github.liewhite.web3.contract.ABIPack

case class ABIString(value: String)

object ABIString {
  def encodeToABIFormat(bytes: Array[Byte]): Array[Byte] = {
    def iter(acc: Array[Byte], rest: Array[Byte]): Array[Byte] = {
      val len = rest.length
      if (len == 0) {
        acc
      } else if (len < 32) {
        acc ++ ABIPack.alignTo32(rest, "right")
      } else {
        iter(acc ++ rest.slice(0, 32), rest.slice(32, len))
      }
    }
    iter(Array.emptyByteArray, bytes)
  }

  given ConvertFromScala[String, ABIString] with {
    def fromScala(s: String): Either[Exception, ABIString] = Right(ABIString(s))
  }

  given ABIPack[ABIString] with {
    def dynamic: Boolean = true
    def pack(a: ABIString): Array[Byte] = {
      val bytes = a.value.getBytes
      val length = bytes.length
      val lengthBytes = ABIPack.alignTo32(BigInt(length).toByteArray, "left")
      val body = encodeToABIFormat(a.value.getBytes)
      lengthBytes ++ body
    }

    def unpack(bytes: Array[Byte]): Either[Exception, ABIString] = {
      val bytesLen = bytes.length
      if (bytesLen < 32) {
        Left(
          Exception(
            "bad bytes length for string ,at least 32, got: " + bytesLen
          )
        )
      } else {
        val lengthBytes = bytes.slice(0, 32)
        val length = BigInt(lengthBytes).toInt
        if (bytesLen < 32 + length) {
          Left(
            Exception(
              s"bad bytes length for string ,at least 32 + ${length}, got: " + bytesLen
            )
          )
        } else {
          Right(ABIString(new String(bytes.slice(32, 32 + length))))
        }
      }
    }
  }
}
