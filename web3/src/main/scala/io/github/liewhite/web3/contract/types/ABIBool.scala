package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.contract.ABIPack

case class ABIBool(value: Boolean)

object ABIBool {
  given ConvertFromScala[Boolean, ABIBool] with {
    def fromScala(s: Boolean): Either[Exception,ABIBool] = Right(ABIBool(s))
  }

  given Conversion[Boolean, ABIBool] with {
    def apply(value: Boolean): ABIBool = ABIBool(value)
  }

  given ABIPack[ABIBool] with {
    def staticSize: Int = 32
    def typeName: String = s"bool"
    def dynamic: Boolean = false
    def pack(a: ABIBool): Array[Byte] = {
      if(a.value) {
        padLeftZero(Array(1.toByte))
      }else{
        padLeftZero(Array(0.toByte))
      }
    }

    def unpack(bytes: Array[Byte]): Either[Exception, ABIBool] = {
      val v = BigInt(bytes) 
      if(v.toInt == 1) {
        Right(ABIBool(true))
      }else{
        Right(ABIBool(false))
      }
    }
  }
}