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
import io.github.liewhite.web3.contract.types.ABIString

class Erc20SymbolTest extends AnyFunSuite {

  val bFunc = ABIFunction[Unit, Tuple1[ABIString]]("symbol")

  test("erc20 balance") {
    assert {
      val token = "0xc778417e063141139fce010982780140aa0cd5ab"
      val receipt = Client.client.read(bFunc)(
        Address.fromHex(token).!,
        ()
      )
      if (receipt.isSuccess) {
        println(receipt.get._1.value)
      }
      receipt.isSuccess
    }
  }
}
