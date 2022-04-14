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

// class DepositTest extends AnyFunSuite {

//     val depositFunc = ABIFunction[Unit, Unit]("deposit")
//     test("deposit") {
//         assert {
//             val deposit = "0xc778417E063141139Fce010982780140Aa0cD5Ab"
//             val receipt = Client.client
//                 .call(depositFunc)(Address.fromHex(deposit).!, (), value = 1 * common.Wei)
//             receipt.isSuccess && receipt.get == ()
//         }
//     }
// }
