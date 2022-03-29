package io.github.liewhite.web3.contract

import org.scalatest.funsuite.AnyFunSuite
import scala.compiletime.constValue


import io.github.liewhite.web3.contract.types.ABIStaticArray
import org.apache.commons.codec.binary.Hex
import io.github.liewhite.web3.contract.types.ABIInt
import io.github.liewhite.web3.contract.types.ABIString


class ABIPackTest extends AnyFunSuite {
  test("pack int") {
    assert {
      val i =  ABIInt[8](BigInt(111),8)
      val result = summon[ABIPack[ABIInt[8]]].pack(i)
      Hex.encodeHex(result).mkString == "000000000000000000000000000000000000000000000000000000000000006f"
    }
  }

  test("unpack int") {
    assert {
      val hexStr = "0000000000000000000000000000000000000000000000000000000000000002"
      val bytes = Hex.decodeHex(hexStr)
      val result = summon[ABIPack[ABIInt[8]]].unpack(bytes)
      result.toOption.get.value == BigInt(2)
    }
  }

  test("pack string") {
    assert {
      val str = "中文 and en"
      val abistr = ABIString(str)
      val result = summon[ABIPack[ABIString]].pack(abistr)
      val hex:String = Hex.encodeHex(result).mkString
      hex == "000000000000000000000000000000000000000000000000000000000000000de4b8ade6968720616e6420656e00000000000000000000000000000000000000"
    }
  }

  test("unpack string") {
    assert {
      val bytes = Hex.decodeHex("000000000000000000000000000000000000000000000000000000000000000de4b8ade6968720616e6420656e00000000000000000000000000000000000000")
      val str = "中文 and en"
      val result = summon[ABIPack[ABIString]].unpack(bytes)
      println(result)
      result.toOption.get.value == str
    }
  }
}