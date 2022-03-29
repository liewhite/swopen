package io.github.liewhite.web3.contract


import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.contract.types.ABIBool

class FunctionTest extends AnyFunSuite {
  test("new function") {
    assert {
      val f = ABIFunction[(ABIBool,ABIBool), ABIBool]("transfer")
      val param = (true,false)
      // f(param)
      // f.pack((true,true))
      true
    }
  }
}