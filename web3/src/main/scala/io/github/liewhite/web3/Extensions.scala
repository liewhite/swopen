package io.github.liewhite.web3

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.types.BytesType
import org.apache.commons.codec.binary.Hex

object Extensions {
  extension (s: String) {
    def toAddress: Either[Exception, Address] = BytesType.fromString[Address](s)
    def toBytes: Either[Exception, Array[Byte]] = {
      val without0x = if(s.startsWith("0x")) s.slice(2,s.length) else s
        try {
            Right(Hex.decodeHex(without0x))
        }catch {
            case e:Exception => Left(e)
        }
    }
  }

  extension (bs: Array[Byte]) {
    def toAddress: Either[Exception, Address] = BytesType.fromBytes[Address](bs)
    def toHex(withPrefix: Boolean = true): Either[Exception, String] = {
      val prefix = if(withPrefix) "0x" else ""
        try {
            Right(prefix + Hex.encodeHex(bs).mkString)
        }catch {
            case e:Exception => Left(e)
        }
    }
  }

}