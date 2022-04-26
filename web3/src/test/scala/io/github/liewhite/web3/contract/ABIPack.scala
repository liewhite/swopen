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


class ABIPackTest extends AnyFunSuite {
  test("int uint convert") {
    assert{
      summon[ConvertFromScala[Int, ABIIntN[8]]]
      summon[ConvertFromScala[Int, ABIUintN[8]]]
      summon[ConvertFromScala[Int, ABIInt]]
      summon[ConvertFromScala[Int, ABIUint]]

      summon[ConvertFromScala[BigInt, ABIIntN[8]]]
      summon[ConvertFromScala[BigInt, ABIUintN[8]]]
      summon[ConvertFromScala[BigInt, ABIInt]]
      summon[ConvertFromScala[BigInt, ABIUint]]
      true
    }
  }
  test("pack int") {
    assert {
      val i =  ABIIntN[8](BigInt(111),8)
      val result = summon[ABIPack[ABIIntN[8]]].pack(i)
      result.toHex() == "0x000000000000000000000000000000000000000000000000000000000000006f"
    }
    assert{
      val i =  ABIInt(BigInt(111))
      val result = summon[ABIPack[ABIInt]].pack(i)
      result.toHex() == "0x000000000000000000000000000000000000000000000000000000000000006f"
    }
  }
  test("pack neg int") {
    assert {
      val i =  ABIIntN[32](BigInt(-1),32)
      val result = summon[ABIPack[ABIIntN[32]]].pack(i)
      result.toHex() == "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
    }
    assert {
      val i =  ABIInt(BigInt(-1))
      val result = summon[ABIPack[ABIInt]].pack(i)
      result.toHex() == "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
    }
  }

  test("unpack int") {
    assert {
      val hexStr = "0000000000000000000000000000000000000000000000000000000000000002"
      val bytes = hexStr.hexToBytes
      val result = summon[ABIPack[ABIIntN[8]]].unpack(bytes)
      result.value == BigInt(2)
    }
    assert {
      val hexStr = "0000000000000000000000000000000000000000000000000000000000000002"
      val bytes = hexStr.hexToBytes
      val result = summon[ABIPack[ABIInt]].unpack(bytes)
      result.value == BigInt(2)
    }
  }
  test("unpack neg int") {
    assert {
      val hexStr = "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
      val bytes = hexStr.hexToBytes
      val result = summon[ABIPack[ABIIntN[8]]].unpack(bytes)
      result.value == BigInt(-1)
    }
    assert {
      val hexStr = "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
      val bytes = hexStr.hexToBytes
      val result = summon[ABIPack[ABIInt]].unpack(bytes)
      result.value == BigInt(-1)
    }
  }

  test("pack uint") {
    assert {
      val i =  ABIUintN[8](BigInt(111),8)
      val result = summon[ABIPack[ABIUintN[8]]].pack(i)
      result.toHex() == "0x000000000000000000000000000000000000000000000000000000000000006f"
    }
    assert{
      val i =  ABIUint(BigInt(111))
      val result = summon[ABIPack[ABIUint]].pack(i)
      result.toHex() == "0x000000000000000000000000000000000000000000000000000000000000006f"
    }
  }
  test("pack neg uint") {
    assertThrows[Exception] {
      val i =  ABIUintN[32](BigInt(-1),32)
      val result = summon[ABIPack[ABIUintN[32]]].pack(i)
      val hex = result.toHex()
      hex == "0x00000000000000000000000000000000000000000000000000000000000000ff"
    }
    assertThrows[Exception] {
      val i =  ABIUint(BigInt(-1))
      val result = summon[ABIPack[ABIUint]].pack(i)
      result.toHex() == "0x00000000000000000000000000000000000000000000000000000000000000ff"
    }
  }

  test("unpack uint") {
    assert {
      val hexStr = "0000000000000000000000000000000000000000000000000000000000000002"
      val bytes = hexStr.hexToBytes
      val result = summon[ABIPack[ABIUintN[8]]].unpack(bytes)
      result.value == BigInt(2)
    }
    assert {
      val hexStr = "0000000000000000000000000000000000000000000000000000000000000002"
      val bytes = hexStr.hexToBytes
      val result = summon[ABIPack[ABIUint]].unpack(bytes)
      result.value == BigInt(2)
    }
  }
  test("unpack neg uint") {
    assert {
      val hexStr = Range(0,32).map(item => "ff").mkString
      val bytes = hexStr.hexToBytes
      val result = summon[ABIPack[ABIUintN[32]]].unpack(bytes)
      println(result.value)
      println(Array.fill(32)(0xff.toByte).toBigUint)
      result.value == Array.fill(32)(0xff.toByte).toBigUint
    }
  }

  test("pack string") {
    assert {
      val str = "中文 and en"
      val abistr = ABIString(str)
      val result = summon[ABIPack[ABIString]].pack(abistr)
      result.toHex() == "0x000000000000000000000000000000000000000000000000000000000000000de4b8ade6968720616e6420656e00000000000000000000000000000000000000"
    }
  }

  test("unpack string") {
    assert {
      val bytes = "0x000000000000000000000000000000000000000000000000000000000000000de4b8ade6968720616e6420656e00000000000000000000000000000000000000".hexToBytes
      val str = "中文 and en"
      val result = summon[ABIPack[ABIString]].unpack(bytes)
      result.value == str
    }
  }

  test("pack address") {
   assert {
      val addrHex = "000f72912fbe295c5155e8b6f94fc6d2c214ee9f"
      val addr = ABIAddress(Address(addrHex))
      val result = summon[ABIPack[ABIAddress]].pack(addr)
      val resultHex = result.toHex(false)
      resultHex.slice(24,64) == addrHex
    }
  }

  test("unpack address") {
    assert {
      val addrHex = "0x000f72912fbe295c5155e8b6f94fc6d2c214ee9f"
      val bytes = addrHex.hexToBytes
      val result = summon[ABIPack[ABIAddress]].unpack(padLeftZero(bytes))
      result.value.toString == addrHex
    }
  }
  test("unpack address with zero prefix") {
    assert {
      val addrHex = "0x000f72912fbe295c5155e8b6f94fc6d2c214ee9f"
      val bytes = addrHex.hexToBytes
      val result = summon[ABIPack[ABIAddress]].unpack(padLeftZero(bytes))
      result.value.toString == addrHex
    }
  }
  test("pack bool") {
   assert {
      val t = summon[ABIPack[ABIBool]].pack(ABIBool(true))
      val tHex = t.toHex(false)
      tHex == "0000000000000000000000000000000000000000000000000000000000000001"
    }
    assert{
      val f = summon[ABIPack[ABIBool]].pack(ABIBool(false))
      val fHex = f.toHex(false)
      fHex == "0000000000000000000000000000000000000000000000000000000000000000"
    }
  }

  test("unpack bool") {
    assert {
      val tHex = "0000000000000000000000000000000000000000000000000000000000000001"
      val tBytes = tHex.hexToBytes
      val result = summon[ABIPack[ABIBool]].unpack(tBytes)
      result.value
    }

    assert {
      val tHex = "0000000000000000000000000000000000000000000000000000000000000000"
      val tBytes = tHex.hexToBytes
      val result = summon[ABIPack[ABIBool]].unpack(tBytes)
      !result.value
    }
  }

  test("unpack static bytes") {
    assert {
      val bytes = "1111111111111111111111111111000000000000000000000000000000000000".hexToBytes
      val result = summon[ABIPack[ABIStaticBytes[14]]].unpack(bytes)
      val target = "1111111111111111111111111111".hexToBytes

      result.value.sameElements(target)
    }
    assert {
      val bytes = "1111111111111111111111111111000000000000000000000000000000000000".hexToBytes
      val result = summon[ABIPack[ABIStaticBytes[15]]].unpack(bytes)
      val target = "1111111111111111111111111111".hexToBytes

      !result.value.sameElements(target)
    }
  }
  test("pack static bytes") {
    assert {
      val bytes = "1111111111111111111111111111".hexToBytes
      val result = summon[ABIPack[ABIStaticBytes[14]]].pack(ABIStaticBytes[14](bytes,14))
      val target = "1111111111111111111111111111000000000000000000000000000000000000".hexToBytes

      result.sameElements(target)
    }
    assert {
      val bytes = "111111111111111111111111111111".hexToBytes
      val result = summon[ABIPack[ABIStaticBytes[15]]].pack(ABIStaticBytes[15](bytes,15))
      val target = "1111111111111111111111111111110000000000000000000000000000000000".hexToBytes

      result.sameElements(target)
    }
  }

  test("pack dynamic bytes") {
    assert {
      val bytes = "222222".hexToBytes
      val result = summon[ABIPack[ABIDynamicBytes]].pack(ABIDynamicBytes(bytes))
      val target = "00000000000000000000000000000000000000000000000000000000000000032222220000000000000000000000000000000000000000000000000000000000".hexToBytes

      result.sameElements(target)
    }
  }

  test("unpack dynamic bytes") {
    assert {
      val bytes = "00000000000000000000000000000000000000000000000000000000000000032222220000000000000000000000000000000000000000000000000000000000".hexToBytes
      val result = summon[ABIPack[ABIDynamicBytes]].unpack(bytes)
      val target = "222222".hexToBytes
      result.value.sameElements(target)
    }
  }

  test("pack static tuple") {
    assert {
      val data = ABIIntN[32](123,32) *: ABIBool(true) *: EmptyTuple
      val bytes = summon[ABIPack[(ABIIntN[32], ABIBool)]].pack(data)
      bytes.toHex(false) == "000000000000000000000000000000000000000000000000000000000000007b0000000000000000000000000000000000000000000000000000000000000001"
    }
  }
  test("unpack static tuple") {
    assert {
      val data = "000000000000000000000000000000000000000000000000000000000000007b0000000000000000000000000000000000000000000000000000000000000001"
      val bytes = data.hexToBytes
      val o = summon[ABIPack[(ABIIntN[32], ABIBool)]].unpack(bytes)
      val v1 = o._1
      val v2 = o._2
      v1.value == 123&& v2.value == true
    }
  }
  test("pack dynamic tuple") {
    assert {
      val data = ABIString("asd") *: ABIBool(true) *: EmptyTuple
      val bytes = summon[ABIPack[(ABIString, ABIBool)]].pack(data)
      bytes.toHex(false) == "0000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000036173640000000000000000000000000000000000000000000000000000000000"
    }
  }

  test("unpack dynamic tuple") {
    assert {
      val data = "0000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000036173640000000000000000000000000000000000000000000000000000000000".hexToBytes
      val decoded = summon[ABIPack[(ABIString, ABIBool)]].unpack(data)
      decoded._1.value == "asd" && decoded._2.value == true
    }
  }

  test("pack static array") {
    assert {
      val data = ABIStaticArray[ABIIntN[8],3](Vector(ABIIntN(3,8),ABIIntN(4,8),ABIIntN(5,8)), 3)
      val bytes = summon[ABIPack[ABIStaticArray[ABIIntN[8],3]]].pack(data)
      bytes.toHex(false) == "000000000000000000000000000000000000000000000000000000000000000300000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000005"
    }
  }

  test("unpack static array") {
    assert {
      val data = "000000000000000000000000000000000000000000000000000000000000000300000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000005".hexToBytes
      val decoded = summon[ABIPack[ABIStaticArray[ABIIntN[8],3]]].unpack(data)
      decoded.value.map(_.value.toInt) == Vector(3,4,5)
    }
  }

  test("pack static array with dynamic elem") {
    assert {
      val data = ABIStaticArray[ABIString,3](Vector(ABIString("a"),ABIString("b"),ABIString("c")), 3)
      val bytes = summon[ABIPack[ABIStaticArray[ABIString,3]]].pack(data)
      bytes.toHex(false) == "000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000161000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001620000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016300000000000000000000000000000000000000000000000000000000000000"
    }
  }

  test("unpack static array with dynamic") {
    assert {
      val data = "000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000161000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001620000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016300000000000000000000000000000000000000000000000000000000000000".hexToBytes
      val decoded = summon[ABIPack[ABIStaticArray[ABIString,3]]].unpack(data)
      decoded.value.map(_.value).toVector == Vector("a","b","c")
    }
  }

  test("pack dynamic array") {
    assert {
      val data = ABIDynamicArray[ABIIntN[8]](Vector(ABIIntN(1,8),ABIIntN(2,8),ABIIntN(3,8)))
      val bytes = summon[ABIPack[ABIDynamicArray[ABIIntN[8]]]].pack(data)
      bytes.toHex(false) == "0000000000000000000000000000000000000000000000000000000000000003000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000003"
    }
  }

  test("unpack dynamic array") {
    assert {
      val data = "0000000000000000000000000000000000000000000000000000000000000003000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000004".hexToBytes
      val decoded = summon[ABIPack[ABIDynamicArray[ABIIntN[8]]]].unpack(data)
      decoded.value.map(_.value.toInt) == Vector(1,2,4)
    }
  }

  test("pack dynamic array with dynamic elem") {
    assert {
      val data = ABIDynamicArray[ABIString](Vector(ABIString("a"),ABIString("b")))
      val bytes = summon[ABIPack[ABIDynamicArray[ABIString]]].pack(data)
      bytes.toHex(false) == "0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000001610000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016200000000000000000000000000000000000000000000000000000000000000"
    }
  }

  test("unpack dynamic array with dynamic elem") {
    assert {
      val data = "0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000001610000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016200000000000000000000000000000000000000000000000000000000000000".hexToBytes
      val decoded = summon[ABIPack[ABIDynamicArray[ABIString]]].unpack(data)
      decoded.value.map(_.value) == Vector("a","b")
    }
  }
}