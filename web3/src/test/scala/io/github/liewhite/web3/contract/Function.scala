package io.github.liewhite.web3.contract


import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.contract.types.ABIBool
import org.apache.commons.codec.binary.Hex
import io.github.liewhite.web3.contract.types.ABIDynamicArray
import io.github.liewhite.web3.contract.types.ABIAddress
import io.github.liewhite.web3.contract.types.{ABIUintN, ABIUint}

class FunctionTest extends AnyFunSuite {
  test("new function") {
    assert {
      val f = ABIFunction[(ABIDynamicArray[ABIAddress],ABIDynamicArray[ABIUint]), ABIBool]("transfer")
      val hex = Hex.encodeHex(f.selector)
      println(hex.mkString)
      println(f.functionSignature)
      true
    }
  }
}