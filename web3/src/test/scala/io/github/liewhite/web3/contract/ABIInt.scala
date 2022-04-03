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


class ABIIntegerTest extends AnyFunSuite {
  test("int to bytes") {
    assert {
      val i = ABIIntN[32](BigInt(1), 32)
      i.toBytes.toHex() == "0x01"
    }
    // assert {
    //   val i = ABIIntN[32](BigInt(-1), 32)
    //   println(i.toBytes.toHex())
    //   i.toBytes.toHex() == "0x01"
    // }
  }

  test("uint to bytes") {
    assert {
      val i = ABIIntN[32](BigInt(1), 32)
      println(i.toBytes.toHex())
      i.toBytes.toHex() == "0x01"
    }
  }
}