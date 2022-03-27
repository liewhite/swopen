package io.github.liewhite.web3.types


import org.scalatest.funsuite.AnyFunSuite

class TxHashTest extends AnyFunSuite {
  test("TxHash") {
    assert {
      TxHash.fromHex("").isLeft
    }
    assert {
      TxHash.fromHex("0xffffff").isLeft
    }
    assert{
      val hex = "0x502430af650037027cf6c44113e34911e9e391365a5aa3a4ceb5f20573791be2"
      val result = TxHash.fromHex(hex).map(_.toHex).map(item => item == hex)
      result.isRight && result.toOption.get
    }
    assert{
      val hex = "0x002430af650037027cf6c44113e34911e9e391365a5aa3a4ceb5f20573791be2"
      val result = TxHash.fromHex(hex).map(_.toHex).map(item => item == hex)
      result.isRight && result.toOption.get
    }
  }
}