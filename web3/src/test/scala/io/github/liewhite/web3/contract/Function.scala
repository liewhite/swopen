package io.github.liewhite.web3.contract


import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.contract.types.ABIBool
import org.apache.commons.codec.binary.Hex
import io.github.liewhite.web3.contract.types.*
import io.github.liewhite.web3.Extensions.*

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

  test("function pack ") {
    assert{
      val f = ABIFunction[(ABIIntN[32],ABIString, ABIStaticArray[ABIBool,2]), ABIInt]("transfer")
      val a: Int = 123
      val b: String = "asd"
      val c = Vector(true,false)
      val params = (a,b,c)
      println(f.packInput(params).toHex)
      true
    }
  }
  test("recursive structure") {
    assert {
      summon[ConvertFromScala[(Int, Vector[(Int,Vector[Int])]), (ABIInt, ABIStaticArray[(ABIInt,ABIStaticArray[ABIInt,2]),2])]]
      true
    }
  }
}