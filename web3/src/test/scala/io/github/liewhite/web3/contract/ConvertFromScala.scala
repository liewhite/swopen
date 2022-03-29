package io.github.liewhite.web3.contract

import org.scalatest.funsuite.AnyFunSuite
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.types.ABIInt
import io.github.liewhite.web3.contract.types.ABIStaticArray
import io.github.liewhite.web3.contract.types.ABIBool

class ConvertFromScalaTest extends AnyFunSuite {
  test("temp") {
    assert {
      SizeValidator.validateSize(10,Some(1),Some(11),Some(1))
      true
    }
  }
  test("convert from scala") {
    assert {
      summon[ConvertFromScala[Int, ABIInt[8]]]
      summon[ConvertFromScala[Boolean, ABIBool]]
      val i =
        summon[ConvertFromScala[Vector[Boolean], ABIStaticArray[ABIBool, 10]]]
      val b =
        summon[ConvertFromScala[Vector[Int], ABIStaticArray[ABIInt[8], 10]]]
      b.fromScala(Vector(1,2,3,4)).toOption.get.length == 10
    }
  }
}
