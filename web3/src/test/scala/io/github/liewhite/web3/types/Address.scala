package io.github.liewhite.web3.types


import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.Extensions.!
import scala.collection.mutable
import io.github.liewhite.json.JsonBehavior.*

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
  test("address equality") {
    assert {
      val a = Address.fromHex("0xBAa79A975DeaF7147cb002A202Fb81BC67cC928f").!
      val b = Address.fromHex("0xbAa79A975DeaF7147cb002A202Fb81BC67cC928f").!
      val m = mutable.Map(a -> 1,b->2)
      val aj = a.encode
      val bj = b.encode
      a == b 
      && a.hashCode == b.hashCode 
      && m.toVector.length == 1 
      && aj.noSpaces == "\"0xBAa79A975DeaF7147cb002A202Fb81BC67cC928f\"".toLowerCase
      && bj.noSpaces == "\"0xBAa79A975DeaF7147cb002A202Fb81BC67cC928f\"".toLowerCase
      && aj.decode[Address].! == a
    }
  }
}