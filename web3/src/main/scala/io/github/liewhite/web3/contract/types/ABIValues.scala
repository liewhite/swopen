package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.ConvertFromScala

// 如果用运行时size， 则编译时无法检查， 比如需要int256， 传入了scala的int， 明显不行， 所以这个given不确定
// 简化的办法是所有需要int，uint的地方都要求传入 自定义的BigInt类型, 然后增加conversion

case class ABIValueBool(value: Boolean)
object ABIValueBool {
  given ConvertFromScala[Boolean, ABIValueBool] with {
    def fromScala(s: Boolean): ABIValueBool = ABIValueBool(s)
  }

  given Conversion[Boolean, ABIValueBool] with {
    def apply(value: Boolean): ABIValueBool = ABIValueBool(value)
  }
}

case class ABIValueInt(value: BigInt, size: Int)
case class ABIValueUint(value: BigInt, size: Int)
case class ABIValueAddress(value: Address)
case class ABIValueBytes(value: Array[Byte])
case class ABIValueNbytes(value: Array[Byte], size: Int)
case class ABIValueString(value: String)
object ABIValueString{
  given ConvertFromScala[String, ABIValueString] with {
    def fromScala(s: String): ABIValueString = ABIValueString(s)
  }
}
// static array
case class ABIValueStaticArray[T](value: Vector[T], size: Int)

// dynamic array
case class ABIValueDynamicArray[T](value: Vector[T])
case class ABIValueMap[K, V](value: Map[K, V])
