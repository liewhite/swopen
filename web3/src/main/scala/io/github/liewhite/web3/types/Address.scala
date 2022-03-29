package io.github.liewhite.web3.types

import scala.math.BigInt
import org.apache.commons.codec.binary.Hex

case class Address(bytes: Array[Byte]) {
  override def toString: String = {
    "0x" + Hex.encodeHex(bytes).mkString
  }
}


object Address {
  given BytesType[Address] with {
    def create(bytes: Array[Byte]): Address = {
      Address(bytes)
    }
    def length: Option[Int] = Some(20)

    def bytes(addr: Address): Array[Byte] = addr.bytes
  }

  def fromHex(hex: String): Either[Exception, Address] = {
    BytesType.fromString[Address](hex)
  }
  def fromBytes(bytes: Array[Byte]): Either[Exception, Address] = {
    BytesType.fromBytes[Address](bytes)
  }
}
