// package io.github.liewhite.web3.rpc

// import scala.language.postfixOps
// import org.scalatest.funsuite.AnyFunSuite
// import org.web3j.crypto.WalletUtils
// import org.web3j.crypto.Bip44WalletUtils
// import io.github.liewhite.web3.Extensions.*
// import io.github.liewhite.web3.wallet.Bip44Wallet
// import io.github.liewhite.web3.common
// import org.web3j.protocol.http.HttpService
// import org.web3j.protocol.Web3j

// class TransferTest extends AnyFunSuite {
//   test("transfer from 0 to 1") {
//     assert {
//       val account1 = Client.wallet.getAccount(1)
//       val result = Client.client.transfer(account1.getAddress,BigDecimal(common.Ether) * 0.1)
//       result.foreach(println)
//       result.isSuccess
//     }
//   }
// }
