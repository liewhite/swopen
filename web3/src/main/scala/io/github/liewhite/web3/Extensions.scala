package io.github.liewhite.web3

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.types.BytesType
import org.apache.commons.codec.binary.Hex

object Extensions {
  extension (s: String) {
    def toAddress: Either[Exception, Address] = BytesType.fromString[Address](s)
    def toBytes: Either[Exception, Array[Byte]] = {
        try {
            Right(Hex.decodeHex(s))
        }catch {
            case e:Exception => Left(e)
        }
    }
  }

  extension (bs: Array[Byte]) {
    def toAddress: Either[Exception, Address] = BytesType.fromBytes[Address](bs)
    def toHex: Either[Exception, String] = {
        try {
            Right(Hex.encodeHex(bs).mkString)
        }catch {
            case e:Exception => Left(e)
        }
    }
  }

}