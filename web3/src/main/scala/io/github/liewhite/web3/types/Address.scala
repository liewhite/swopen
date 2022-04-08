package io.github.liewhite.web3.types

import scala.math.BigInt
import org.apache.commons.codec.binary.Hex
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.json.codec.Encoder
import io.circe.Json
import io.github.liewhite.json.codec.Decoder
import io.github.liewhite.json.codec.DecodeException
import io.getquill.MappedEncoding
import io.github.liewhite.sqlx.TField
import java.sql.SQLData
import org.jooq.impl.SQLDataType
import org.jooq.DataType

class Address(val bytes: Array[Byte]) {
  override def toString: String = {
    bytes.toHex()
  }

  def lowerCaseString: String = {
    toString.toLowerCase
  }
  override def equals(that: Any): Boolean = bytes.sameElements(that.asInstanceOf[Address].bytes)

  override def hashCode: Int = {
    bytes.toHex().hashCode
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
  given Encoder[Address] with {
    def encode(t: Address): Json = Json.fromString(t.bytes.toHex())
  }
  given Decoder[Address] with {
    def decode(
        data: Json,
        withDefaults: Boolean = true
    ): Either[DecodeException, Address] = {
      data.asString.flatMap(s => Address.fromHex(s).toOption) match {
        case Some(o) => Right(o)
        case None => Left(DecodeException("failed decode address from: " + data))
      }
    }
  }

  given MappedEncoding[String, Address](item => Address.fromHex(item).!)
  given MappedEncoding[Address, String](_.toString)
  given TField[Address] with {
    def dataType: DataType[_] = SQLDataType.CHAR(42)
  }
}
