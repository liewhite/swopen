package io.github.liewhite.web3.types

import org.scalatest.funsuite.AnyFunSuite

class TxHashTest extends AnyFunSuite {
    test("TxHash") {
        assertThrows[Exception] {
            TxHash("")
        }
        assertThrows[Exception] {
            TxHash("0xffffff")
        }
        assert {
            val hex = "0x502430af650037027cf6c44113e34911e9e391365a5aa3a4ceb5f20573791be2"
            TxHash(hex).toHex == hex
        }
        assert {
            val hex = "0x002430af650037027cf6c44113e34911e9e391365a5aa3a4ceb5f20573791be2"
            TxHash(hex).toHex == hex
        }
    }
}
