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
// import io.github.liewhite.web3.contract.ABIFunction
// import io.github.liewhite.web3.contract.types.ABIAddress
// import io.github.liewhite.web3.contract.types.ABIUintN
// import io.github.liewhite.web3.types.Address

// class Erc20BalanceTest extends AnyFunSuite {

//   val bFunc = ABIFunction[Tuple1[ABIAddress], ABIUintN[256]]("balanceOf")

//   test("erc20 balance") {
//     assert {
//       val token = "0xc778417e063141139fce010982780140aa0cd5ab"
//       val receipt = Client.client.read(bFunc)(
//         Address.fromHex(token).!,
//         Tuple1(Client.wallet.getAccount(0).address),
//         // Tuple1(Address.fromHex("0xaba462f5d4170db0a4b58fbc1567abad3a186d24").!)
//         block = Some(BigInt(12162606))
//       )
//       if (receipt.isSuccess) {
//         println(receipt.get.value)
//       }
//       receipt.isSuccess
//     }
//   }
// }
