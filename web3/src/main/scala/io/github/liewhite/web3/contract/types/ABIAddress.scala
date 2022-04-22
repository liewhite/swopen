package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.types.BytesType
import io.github.liewhite.web3.contract.ABIPack

case class ABIAddress(value: Address) extends ABIType

object ABIAddress {
  given ConvertFromScala[String, ABIAddress] with {
    def fromScala(s: String): ABIAddress = {
      ABIAddress(Address(s))
    }
  }
  given ConvertFromScala[Array[Byte], ABIAddress] with {
    def fromScala(s: Array[Byte]): ABIAddress = {
      ABIAddress(Address(s))
    }
  }

  given ConvertFromScala[Address, ABIAddress] with {
    def fromScala(s: Address): ABIAddress = {
      ABIAddress(s)
    }
  }

  given ABIPack[ABIAddress] with {
    def staticSize: Int = 32
    def typeName: String = s"address"
    def dynamic: Boolean = false
    def pack(a: ABIAddress): Array[Byte] = {
      padAddress(a)
    }
    def unpack(bytes: Array[Byte]): ABIAddress = {
      if(bytes.length != 32) {
        throw Exception("address in abi encoding must be 32 bytes, got" + bytes.length)
      }

      if(bytes.slice(0,12).toBigUint != 0) {
        throw Exception("address in abi encoding must start with 12 zero bytes")
      }
      ABIAddress(Address(bytes.slice(12,32)))
    }
  }
}
