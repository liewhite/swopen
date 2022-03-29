package io.github.liewhite.web3.contract

import org.scalatest.funsuite.AnyFunSuite
import scala.compiletime.constValue
import io.github.liewhite.web3.contract.types.ABIInt
import io.github.liewhite.web3.contract.types.ABIStaticArray
import io.github.liewhite.web3.contract.types.ABIBool

class SizeValidatorTest extends AnyFunSuite {
  test("temp") {
    assert {
      SizeValidator.validateSize(10,Some(1),Some(10),Some(5))
      true
    }
    assertCompiles {
      "SizeValidator.validateSize(32,Some(1),Some(256),Some(8))"
    }
    assertDoesNotCompile {
      "SizeValidator.validateSize(0,Some(1),Some(11),Some(1))"
    }
    assertDoesNotCompile {
      "SizeValidator.validateSize(3,Some(0),Some(2),Some(1))"
    }
    assertDoesNotCompile {
      "SizeValidator.validateSize(3,Some(0),Some(4),Some(2))"
    }
    assertDoesNotCompile {
      "SizeValidator.validateSize(4,Some(0),Some(3),Some(2))"
    }
  }
}
