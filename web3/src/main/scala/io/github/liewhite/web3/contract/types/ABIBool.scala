package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.ConvertFromScala

// 如果用运行时size， 则编译时无法检查， 比如需要int256， 传入了scala的int， 明显不行， 所以这个given不确定
// 简化的办法是所有需要int，uint的地方都要求传入 自定义的BigInt类型, 然后增加conversion

case class ABIBool(value: Boolean)

object ABIBool {
  given ConvertFromScala[Boolean, ABIBool] with {
    def fromScala(s: Boolean): Either[Exception,ABIBool] = Right(ABIBool(s))
  }

  given Conversion[Boolean, ABIBool] with {
    def apply(value: Boolean): ABIBool = ABIBool(value)
  }
}