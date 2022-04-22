package io.github.liewhite.web3
import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.Extensions.*
import scala.math
import scala.language.postfixOps

class ABIPackTest extends AnyFunSuite {
  test("convert bytes to hex") {
    assert {
      val hex = "0xffff"
      val bytes = hex.toBytes
      bytes.toHex(true) == hex
    }
  }
  test("hex to bytes") {
    assert {
      val odd = "0x0fffff"
      val even = "0xfffff"
      odd.toBytes.toBigInt == even.toBytes.toBigInt
      &&  odd.toBytes.toBigUint == even.toBytes.toBigUint
    }
  }


  test("bytes to uint") {
    assert {
      val hex = "0xffff".toBytes
      val i = hex.toBigUint
      i == BigInt(0xffff)
    }
    assert {
      val hex = "0x00ffff".toBytes
      val i = hex.toBigUint
      i == BigInt(0xffff)
    }
    assert {
      val hex = Array.fill(256)(0xff.toByte)
      val i = hex.toBigUint
      i == BigInt(Array[Byte](0) ++ hex)
    }
  }

  test("bytes to int") {
    assert {
      val hex = "0xffff".toBytes
      val i = hex.toBigInt
      i == -1
    }

    assert {
      val hex = "0x00ffff".toBytes
      val i = hex.toBigInt
      i == BigInt(0xffff)
    }

    assert {
      val hex = Array.fill(256)(0xff.toByte)
      val i = hex.toBigInt
      i == BigInt(-1)
    }
  }
}
