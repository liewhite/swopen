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


class ABIPackIntegerTest extends AnyFunSuite {
  test("pack max uint") {
    assert {
      val data = Array.fill(32)(0xff.toByte)
      val decoder = summon[ABIPack[ABIUint]].unpack(data)
      decoder.value == (Array[Byte](0.toByte) ++ Array.fill(32)(0xff.toByte)).toBigUint
    }
  }
}