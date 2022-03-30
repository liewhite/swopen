package io.github.liewhite.web3
import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.Extensions.*


class ABIPackTest extends AnyFunSuite {
  test("convert bytes to hex") {
    assert{
        val hex = "ffff"
        val bytes = hex.toBytes.toOption.get
        bytes.toHex.toOption.get == hex
        
    }
}}