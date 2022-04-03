package io.github.liewhite.web3.common

import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.Extensions.*
import scala.math

class PaddingTest extends AnyFunSuite {
  test("padding normal uint") {
    assert {
      val i = BigInt(1)
      val result = i.toIntByte32.get.toHex()
      result == (Array.fill(31)(0x00.toByte) ++ Array[Byte](1.toByte)).toHex()
    }
  }

  test("padding zero uint") {
    assert {
      val i = BigInt(0)
      val result = i.toUintByte32.get.toHex()
      result == Array.fill(32)(0x00.toByte).toHex()
    }
  }

  test("padding max uint") {
    assert {
      val i = BigInt(Array[Byte](0) ++ Array.fill(32)(0xff.toByte))
      val result = i.toUintByte32.get.toHex()
      result == Array.fill(32)(0xff.toByte).toHex()

    }
  }

  test("padding negative uint") {
    assertThrows[Exception] {
      val i = BigInt(-1)
      i.toUintByte32.get.toHex()
    }
  }

  // int
  test("padding normal int") {
    assert {
      val i = BigInt(1)
      val result = i.toUintByte32.get.toHex()
      result == (Array.fill(31)(0x00.toByte) ++ Array[Byte](1.toByte)).toHex()
    }
  }

  test("padding zero int") {
    assert {
      val i = BigInt(0)
      val result = i.toIntByte32.get.toHex()
      result == Array.fill(32)(0x00.toByte).toHex()
    }
  }

  test("padding max int") {
    assert {
      val i = BigInt(Array[Byte]((0xff >> 1).toByte) ++ Array.fill(31)(0xff.toByte))
      val result = i.toIntByte32.get.toHex()
      result == (Array[Byte]((0xff >> 1).toByte) ++ Array.fill(31)(0xff.toByte)).toHex()
    }
  }

  test("padding negative int") {
    assert {
      val i = BigInt(-1)
      i.toIntByte32.get.toHex() == Array.fill(32)(0xff.toByte).toHex()
    }
  }
}