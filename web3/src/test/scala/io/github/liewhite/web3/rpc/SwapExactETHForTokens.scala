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

// class SwapExactETHForTokensTest extends AnyFunSuite {

//   val f = ABIFunction[
//     (ABIUintN[256], ABIDynamicArray[ABIAddress], ABIAddress, ABIUintN[256]),
//     Unit
//   ]("swapExactETHForTokens")
//   test("swapExactETHForTokens") {
//     assert {
//       val receipt = Client.client.transact(f)(
//         Address.fromHex("0x7a250d5630b4cf539739df2c5dacb4c659f2488d").!,
//         (
//           "0x01".toBytes.!.toBigUint.!,
//           Vector(
//             Address.fromHex("0xc778417e063141139fce010982780140aa0cd5ab").!,
//             Address.fromHex("0xaD6D458402F60fD3Bd25163575031ACDce07538D").!
//           ),
//           Address.fromHex("0x6ac39785f4145b595313840dc6e1d2a753d87e1f").!,
//           "0x1a6f117391".toBytes.!.toBigUint.!,
//         ),
//         value = common.GWei * 200_000_000
//       )
//       println(receipt)
//       receipt.isSuccess && receipt.get.isStatusOK
//     }
//   }
// }
