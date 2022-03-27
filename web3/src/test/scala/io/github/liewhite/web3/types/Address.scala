package io.github.liewhite.web3.types


import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.types.Address

class AddressTest extends AnyFunSuite {
  test("Address") {
    assert {
      Address.fromHex("").isLeft
    }

    assert {
      Address.fromHex("0xffffff").isLeft
    }
    assert{
      val addr = "0xf88140b1f0fa5d2100492b3dc182b1b0c987873b"
      val result = Address.fromHex(addr).map(_.toHex).map(item => item == addr)
      result.isRight && result.toOption.get
    }
    assert{
      val addr = "0x000140b1f0fa5d2100492b3dc182b1b0c987873b"
      val result = Address.fromHex(addr).map(_.toHex).map(item => item == addr)
      result.isRight && result.toOption.get
    }
  }
}