package io.github.liewhite.web3.types

import scala.math.BigInt
import org.apache.commons.codec.binary.Hex
import io.github.liewhite.web3.Extensions.*
import io.getquill.MappedEncoding
import io.github.liewhite.sqlx.TField
import java.sql.SQLData
import org.jooq.impl.SQLDataType
import org.jooq.DataType
import org.web3j.crypto.Keys
import zio.json._

class Address(bytes: Array[Byte]) extends BytesType(bytes,20) {
    def this(hex: String) = {
        this(hex.toBytes)
    }

    def toCheckSum: String = {
        Keys.toChecksumAddress(toString)
    }
}

object Address {
    given JsonEncoder[Address] = JsonEncoder.string.contramap(_.toHex)
    given JsonDecoder[Address] = JsonDecoder.string.map(Address(_))

    given MappedEncoding[String, Address](item => Address(item))

    given MappedEncoding[Address, String](_.toString)

    given TField[Address] with {
        def dataType: DataType[_] = SQLDataType.CHAR(42)
    }
}
