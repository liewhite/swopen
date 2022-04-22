package io.github.liewhite.web3.types

import org.scalatest.funsuite.AnyFunSuite
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.Extensions.!
import io.github.liewhite.json.SwopenJson.*
import scala.collection.mutable

class AddressTest extends AnyFunSuite {
    test("Address") {
        assertThrows[Exception] {
            Address("")
        }

        assertThrows[Exception] {
            Address("0xffffff")
        }

        assert {
            val addr = "0xf88140b1f0fa5d2100492b3dc182b1b0c987873b"
            Address(addr).toHex == addr
        }

        assert {
            val addr = "0x000140b1f0fa5d2100492b3dc182b1b0c987873b"
            Address(addr).toHex == addr
        }
    }
    test("address equality") {
        assert {
            val a  = Address("0xBAa79A975DeaF7147cb002A202Fb81BC67cC928f")
            val b  = Address("0xbAa79A975DeaF7147cb002A202Fb81BC67cC928f")
            val m  = mutable.Map(a -> 1, b -> 2)
            val aj = a.toJsonStr()
            val bj = b.toJsonStr()
            a == b
            && a.hashCode == b.hashCode
            && m.toVector.length == 1
            && aj == "\"0xBAa79A975DeaF7147cb002A202Fb81BC67cC928f\"".toLowerCase
            && bj == "\"0xBAa79A975DeaF7147cb002A202Fb81BC67cC928f\"".toLowerCase
            && read[Address](aj) == a
        }
    }
}
