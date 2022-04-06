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

// class Erc20TransferTest extends AnyFunSuite {

//   val transferFunc = ABIFunction[(ABIAddress,ABIUintN[256]),Unit]("transfer")

//   val depositFunc = ABIFunction[Unit,Unit]("deposit")

//   test("erc20 transfer") {
//     assert {
//       val token = "0xaD6D458402F60fD3Bd25163575031ACDce07538D"
//       val receipt = Client.client.transact(transferFunc)(Address.fromHex(token).!, (Client.wallet.getAccount(1).address, BigInt("111111111111111111111111")))
//       receipt.isSuccess && receipt.get.isStatusOK
//     }
//   }
//   // test("deposit"){
//   //   assert {
//   //     val deposit = "0xc778417E063141139Fce010982780140Aa0cD5Ab"
//   //     val receipt = client.transact(depositFunc)(Address.fromHex(deposit).!,(),value = common.GWei)
//   //     println(receipt.get)
//   //     true
//   //   }
//   // }
// }