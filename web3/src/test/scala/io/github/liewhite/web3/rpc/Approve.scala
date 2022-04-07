package io.github.liewhite.web3.rpc

import scala.language.postfixOps
import org.scalatest.funsuite.AnyFunSuite
import org.web3j.crypto.WalletUtils
import org.web3j.crypto.Bip44WalletUtils
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.wallet.Bip44Wallet
import io.github.liewhite.web3.common
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3j
import io.github.liewhite.web3.contract.ABIFunction
import io.github.liewhite.web3.contract.types.ABIAddress
import io.github.liewhite.web3.contract.types.ABIUintN
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.types.ABIDynamicArray

class ApproveTest extends AnyFunSuite {

  val f = ABIFunction[
    (ABIAddress, ABIUintN[256]),
    Unit
  ]("approve")
  test("approve") {
    assert {
      val receipt = Client.client.transact(f)(
        Address.fromHex("0xc778417E063141139Fce010982780140Aa0cD5Ab").!,
        (
          Address.fromHex("0x68b3465833fb72a70ecdf485e0e4c7bd8665fc45").!,
          "0x1a6191".toBytes.!.toBigUint.!,
        ),
      )
      println(receipt)
    //   receipt.isSuccess && receipt.get.isStatusOK
      receipt.isSuccess && receipt.get.isStatusOK

    }
  }
}
