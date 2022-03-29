package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.ConvertFromScala
import io.github.liewhite.web3.types.BytesType
import io.github.liewhite.web3.contract.ABIPack

case class ABIAddress(value: Address)

object ABIAddress {
  given ConvertFromScala[String, ABIAddress] with {
    def fromScala(s: String): Either[Exception, ABIAddress] = {
      BytesType.fromString[Address](s).map(ABIAddress(_))
    }
  }
  given ConvertFromScala[Array[Byte], ABIAddress] with {
    def fromScala(s: Array[Byte]): Either[Exception, ABIAddress] = {
      BytesType.fromBytes[Address](s).map(ABIAddress(_))
    }
  }
  given ABIPack[ABIAddress] with {
    def dynamic: Boolean = false
    def pack(a: ABIAddress): Array[Byte] = {
      a.value.bytes
    }
    def unpack(bytes: Array[Byte]): Either[Exception, ABIAddress] = {
      BytesType.fromBytes[Address](bytes).map(ABIAddress(_))
    }
  }
}
