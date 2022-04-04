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

class TransferTest extends AnyFunSuite {
  test("transfer from 0 to 1") {
    assert {
      val account0 = wallet.getAccount(0)
      val account1 = wallet.getAccount(1)
      val hp = new HttpService("https://ropsten.infura.io/v3/81e90c9cd6a0430182e3a2bec37f2ba0")
      val web3 = Web3j.build(hp)
      val client = Web3ClientWithCredential(web3, account0)
      val result = client.transfer(account1.address,BigDecimal(common.Ether) * 0.1)
      result.foreach(println)
      result.isSuccess
    }
  }
}
