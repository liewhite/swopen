package io.github.liewhite.web3.types

import scala.math.BigInt
import org.apache.commons.codec.binary.Hex

case class TxHash(bytes: Array[Byte])

object TxHash{
  given BytesType[TxHash] with {
    def create(bytes: Array[Byte]): TxHash = {
      TxHash(bytes)
    }
    def length: Option[Int] = Some(32)

    def bytes(h: TxHash): Array[Byte] = h.bytes
  }

  def fromHex(hex: String): Either[Exception, TxHash] = {
    BytesType.fromString[TxHash](hex)
  }
  def fromBytes(bytes: Array[Byte]): Either[Exception, TxHash] = {
    BytesType.fromBytes[TxHash](bytes)
  }
}