package io.github.liewhite.web3.contract

import org.scalatest.funsuite.AnyFunSuite
import scala.compiletime.constValue


import io.github.liewhite.web3.contract.types.ABIStaticArray
import org.apache.commons.codec.binary.Hex
import io.github.liewhite.web3.contract.types.ABIInt
import io.github.liewhite.web3.contract.types.ABIString
import io.github.liewhite.web3.contract.types.ABIAddress
import io.github.liewhite.web3.types.BytesType
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.types.ABIBool
import io.github.liewhite.web3.contract.types.ABIStaticBytes
import io.github.liewhite.web3.contract.types.ABIDynamicBytes


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
      result.toOption.get.value == str
    }
  }

  test("pack address") {
   assert {
      val addrHex = "0x000f72912fbe295c5155e8b6f94fc6d2c214ee9f"
      val addr = ABIAddress(BytesType.fromString[Address](addrHex).toOption.get)
      val result = summon[ABIPack[ABIAddress]].pack(addr)
      val resultHex = Hex.encodeHex(result).mkString
      resultHex == addrHex.substring(2)
    }
  }

  test("unpack address") {
    assert {
      val addrHex = "0x000f72912fbe295c5155e8b6f94fc6d2c214ee9f"
      val bytes = Hex.decodeHex(addrHex.substring(2))
      val result = summon[ABIPack[ABIAddress]].unpack(bytes)
      result.toOption.get.value.toString == addrHex
    }
  }
  test("pack bool") {
   assert {
      val t = summon[ABIPack[ABIBool]].pack(ABIBool(true))
      val tHex = Hex.encodeHex(t).mkString
      tHex == "0000000000000000000000000000000000000000000000000000000000000001"
    }
    assert{
      val f = summon[ABIPack[ABIBool]].pack(ABIBool(false))
      val fHex = Hex.encodeHex(f).mkString
      fHex == "0000000000000000000000000000000000000000000000000000000000000000"
    }
  }

  test("unpack bool") {
    assert {
      val tHex = "0000000000000000000000000000000000000000000000000000000000000001"
      val tBytes = Hex.decodeHex(tHex)
      val result = summon[ABIPack[ABIBool]].unpack(tBytes)
      result.toOption.get.value
    }

    assert {
      val tHex = "0000000000000000000000000000000000000000000000000000000000000000"
      val tBytes = Hex.decodeHex(tHex)
      val result = summon[ABIPack[ABIBool]].unpack(tBytes)
      !result.toOption.get.value
    }
  }

  test("unpack static bytes") {
    assert {
      val bytes = Hex.decodeHex("1111111111111111111111111111000000000000000000000000000000000000")
      val result = summon[ABIPack[ABIStaticBytes[14]]].unpack(bytes)
      val target = Hex.decodeHex("1111111111111111111111111111")

      result.toOption.get.value.sameElements(target)
    }
    assert {
      val bytes = Hex.decodeHex("1111111111111111111111111111000000000000000000000000000000000000")
      val result = summon[ABIPack[ABIStaticBytes[15]]].unpack(bytes)
      val target = Hex.decodeHex("1111111111111111111111111111")

      !result.toOption.get.value.sameElements(target)
    }
  }
  test("pack static bytes") {
    assert {
      val bytes = Hex.decodeHex("1111111111111111111111111111")
      val result = summon[ABIPack[ABIStaticBytes[14]]].pack(ABIStaticBytes[14](bytes,14))
      val target = Hex.decodeHex("1111111111111111111111111111000000000000000000000000000000000000")

      result.sameElements(target)
    }
    assert {
      val bytes = Hex.decodeHex("111111111111111111111111111111")
      val result = summon[ABIPack[ABIStaticBytes[15]]].pack(ABIStaticBytes[15](bytes,15))
      val target = Hex.decodeHex("1111111111111111111111111111110000000000000000000000000000000000")

      result.sameElements(target)
    }
  }

  test("pack dynamic bytes") {
    assert {
      val bytes = Hex.decodeHex("222222")
      val result = summon[ABIPack[ABIDynamicBytes]].pack(ABIDynamicBytes(bytes))
      val target = Hex.decodeHex("00000000000000000000000000000000000000000000000000000000000000032222220000000000000000000000000000000000000000000000000000000000")

      result.sameElements(target)
    }
  }

  test("unpack dynamic bytes") {
    assert {
      val bytes = Hex.decodeHex("00000000000000000000000000000000000000000000000000000000000000032222220000000000000000000000000000000000000000000000000000000000")
      val result = summon[ABIPack[ABIDynamicBytes]].unpack(bytes)
      val target = Hex.decodeHex("222222")
      result.toOption.get.value.sameElements(target)
    }
  }
}