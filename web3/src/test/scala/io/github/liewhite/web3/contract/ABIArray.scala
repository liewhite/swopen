package io.github.liewhite.web3.contract

import org.scalatest.funsuite.AnyFunSuite
import scala.compiletime.constValue


import io.github.liewhite.web3.contract.types.ABIStaticArray
import io.github.liewhite.web3.contract.types.ABIIntN
import io.github.liewhite.web3.contract.types.ABIString
import io.github.liewhite.web3.contract.types.ABIAddress
import io.github.liewhite.web3.types.BytesType
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.types.*
import io.github.liewhite.web3.common.*
import io.github.liewhite.web3.Extensions.*


class ABIArrayTest extends AnyFunSuite {
  test("dynamic array of address") {
    assert {
      val data = "0x0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000bb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c0000000000000000000000008e876f6cd486508814cb5e2ffaeeca0bce58f506"
      val p = summon[ABIPack[ABIDynamicArray[ABIAddress]]]
      println(p.unpack(data.toBytes.toOption.get).toOption.get.value.map(_.value))
      true
    }
    // assert {
    //   val i = ABIIntN[32](BigInt(-1), 32)
    //   println(i.toBytes.toHex())
    //   i.toBytes.toHex() == "0x01"
    // }
  }
}