package io.github.liewhite.web3
import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.Extensions.*
import scala.math

class ABIPackTest extends AnyFunSuite {
  test("convert bytes to hex") {
    assert {
      val hex = "0xffff"
      val bytes = hex.toBytes.toOption.get
      bytes.toHex(true) == hex
    }
  }

  test("bytes to uint") {
    assert {
      val hex = "0xffff".toBytes.toOption.get
      val i = hex.toBigUint
      i == BigInt(0xffff)
    }
    assert {
      val hex = "0x00ffff".toBytes.toOption.get
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
      val hex = "0xffff".toBytes.toOption.get
      val i = hex.toBigInt
      i == -1
    }

    assert {
      val hex = "0x00ffff".toBytes.toOption.get
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
