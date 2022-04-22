package io.github.liewhite.web3.types

import scala.math.BigInt
import org.apache.commons.codec.binary.Hex
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.json.SwopenJson.*
import io.getquill.MappedEncoding
import io.github.liewhite.sqlx.TField
import java.sql.SQLData
import org.jooq.impl.SQLDataType
import org.jooq.DataType
import org.web3j.crypto.Keys
import upickle.core.Visitor

class Address(bytes: Array[Byte]) extends BytesType(bytes,20) {
    def this(hex: String) = {
        this(hex.toBytes)
    }

    def toCheckSum: String = {
        Keys.toChecksumAddress(toString)
    }
}

object Address {
    given Writer[Address] with {
        def write0[V](
            out: Visitor[_, V],
            v: Address
        ): V = {
            out.visitString(v.toString() ,-1)
        }
    }

    given Reader[Address] = {
        new Reader.Delegate[Any, Address](summon[Reader[String]].map(s => {
            Address(s)
        }))
    }

    given MappedEncoding[String, Address](item => Address(item))

    given MappedEncoding[Address, String](_.toString)

    given TField[Address] with {
        def dataType: DataType[_] = SQLDataType.CHAR(42)
    }
}
