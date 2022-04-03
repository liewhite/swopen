package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.common.*
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
    def staticSize: Int = 32

    def typeName: String = s"address"
    def dynamic: Boolean = false
    def pack(a: ABIAddress): Array[Byte] = {
      padAddress(a)
    }
    def unpack(bytes: Array[Byte]): Either[Exception, ABIAddress] = {
      if(bytes.length != 32) {
        return Left(Exception("address in abi encoding must be 32 bytes, got" + bytes.length))
      }
      if(!bytes.slice(0,12).toBigUint.isDefined) {
        return Left(Exception("address in abi encoding must start with 12 zero bytes"))
      }
      if(bytes.slice(0,12).toBigUint.get != 0) {
        return Left(Exception("address in abi encoding must start with 12 zero bytes"))
      }
      BytesType.fromBytes[Address](bytes.slice(12,32)).map(ABIAddress(_))
    }
  }
}
