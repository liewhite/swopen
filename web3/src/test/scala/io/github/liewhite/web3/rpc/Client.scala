package io.github.liewhite.web3.rpc

import scala.language.postfixOps
import io.github.liewhite.web3.Extensions.*

import io.github.liewhite.web3.wallet.Bip44Wallet
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

object Client {
  val wallet = Bip44Wallet
    .fromMnemonic(
      "finish universe napkin torch blur movie approve inspire purse easily false same",
      ""
    ) !

  val hp = new HttpService(
    // "https://speedy-nodes-nyc.moralis.io/e8f1edbf0ef6e4a9c3c72015/eth/ropsten/archive"
    "https://ropsten.infura.io/v3/81e90c9cd6a0430182e3a2bec37f2ba0"
    // "https://eth-hk1.csnodes.com/v1/973eeba6738a7d8c3bd54f91adcbea89"
  )
  val web3 = Web3j.build(hp)
  val readonlyClient = Web3Client(web3, wallet.getAccount(0).getAddress)
  val client = Web3ClientWithCredential(web3, wallet.getAccount(0))

}
