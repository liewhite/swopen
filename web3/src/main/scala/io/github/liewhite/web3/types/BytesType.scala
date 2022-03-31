package io.github.liewhite.web3.types

import org.apache.commons.codec.binary.Hex

trait BytesType[T] {
  def create(bytes: Array[Byte]): T
  def length: Option[Int]
  def bytes(t: T): Array[Byte]

  extension [T: BytesType](data: T) {
    def toHex: String = {
      "0x" + Hex.encodeHex(summon[BytesType[T]].bytes(data)).mkString
    }
  }
}

object BytesType {
  def fromBytes[T](
      bytes: Array[Byte]
  )(using factory: BytesType[T]): Either[Exception, T] = {
    if (factory.length.isDefined && bytes.length != factory.length.get) {
      println(factory.length.get)
      println(bytes.length)
      Left(Exception(s"bytes length must be ${factory.length.get}, got: ${bytes.length}"))
    } else {
      Right(factory.create(bytes))
    }
  }

  def fromString[T](
      hex: String
  )(using factory: BytesType[T]): Either[Exception, T] = {
    val hexWithout0x = if (hex.startsWith("0x")) hex.substring(2) else hex
    try {
      fromBytes(Hex.decodeHex(hexWithout0x))
    } catch {
      case e: Exception => {
        Left(e)
      }
    }
  }

}
