package io.github.liewhite.web3.rpc

import scala.language.postfixOps
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.ABIFunction
import io.github.liewhite.web3.contract.ABIPack
import io.github.liewhite.web3.common.EthValue
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.request.Transaction
import io.github.liewhite.web3.wallet.Bip44Wallet
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import scala.util.Try
import org.web3j.crypto.RawTransaction
import org.web3j.tx.RawTransactionManager
import io.github.liewhite.web3.wallet.Account
import io.github.liewhite.web3.Extensions.*
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import io.github.liewhite.web3.types.TxHash
import io.github.liewhite.web3.common.ConvertFromScala
import org.web3j.protocol.core.DefaultBlockParameterNumber
import scala.concurrent.duration.Duration
import org.web3j.protocol.core.methods.response.TransactionReceipt
import com.typesafe.scalalogging.Logger
import java.math.BigInteger

case class SendTransactionOption(
)
class Web3ClientWithCredential(
    client: Web3j,
    account: Account,
    retries: Int = 20,
    sleepDuration: Int = 15 * 1000
) extends RawTransactionManager(
      client,
      account.toCredential,
      retries,
      sleepDuration
    ) {
  private val logger = Logger("transaction")
  private val rawTransactionManger =
    new RawTransactionManager(client, account.toCredential)

  def transfer(to: Address, value: BigDecimal): Try[TxHash] = {
    val t = new Transfer(client, this).sendFunds(
      to.toString,
      value.bigDecimal,
      Convert.Unit.WEI
    )
    Try({
      val receipt = t.send
      TxHash.fromHex(receipt.getTransactionHash).toOption.get
    })
  }

  def transact[IN, OUT, T](function: ABIFunction[IN, OUT])(
      to: Address,
      params: T,
      nonce: Option[BigInt] = None,
      gasPrice: Option[BigInt] = None,
      gasLimit: Option[BigInt] = None,
      //   baseFee: Option[BigInt] = None,
      //   maxPriorityFee: Option[BigInt] = None,
      value: BigInt = 0
  )(using converter: ConvertFromScala[T, IN]): Try[TransactionReceipt] = {
    Try {
      val input = converter.fromScala(params).!
      val inputString =
        (function.selector ++ function.packInput(params)).toHex()

      val nonceValue = nonce match {
        case Some(o) => o
        case None => {
          val n: BigInt = client
            .ethGetTransactionCount(
              account.address.toString,
              DefaultBlockParameterName.LATEST
            )
            .send
            .getTransactionCount
          n
        }
      }
      val gasPriceValue = gasPrice match {
        case Some(o) => o
        case None => {
          val n: BigInt = client.ethGasPrice().send.getGasPrice
          n
        }
      }
      val gasLimitValue = gasLimit match {
        case Some(o) => o
        case None => {
          val call =
            Transaction.createFunctionCallTransaction(
              account.address.toString,
              nonceValue.bigInteger,
              gasPriceValue.bigInteger,
              BigInteger.valueOf(0),
              to.toString,
              value.bigInteger,
              inputString
            )
          val estimateResult =
            client
              .ethEstimateGas(call)
              .send
          if (estimateResult.hasError) {
            throw Exception(
              "estimate gas err:" + estimateResult.getError.getMessage
            )
          }
          val n: BigInt = estimateResult.getAmountUsed
          n
        }
      }
      val tx = RawTransaction.createTransaction(
        nonceValue.bigInteger,
        gasPriceValue.bigInteger,
        gasLimitValue.bigInteger,
        to.toString,
        value.bigInteger,
        inputString
      )
      val sendResult = signAndSend(tx)
      logger.info("tx hash:" + sendResult.getTransactionHash)
      processResponse(sendResult)
    }
  }

  def call[IN, OUT, T](function: ABIFunction[IN, OUT])(
      to: Address,
      params: T,
      nonce: Option[BigInt] = None,
      gasPrice: Option[BigInt] = None,
      gasLimit: Option[BigInt] = None,
      value: BigInt = 0,
      block: Option[BigInt] = None
  )(using converter: ConvertFromScala[T, IN]): Try[OUT] = {
    val input = converter.fromScala(params).!
    val inputString = function.packInputWithSelector(params).toHex()
    logger.info("input:" + inputString)

    val nonceValue = nonce match {
      case Some(o) => o
      case None => {
        val n: BigInt = client
          .ethGetTransactionCount(
            account.address.toString,
            DefaultBlockParameterName.LATEST
          )
          .send
          .getTransactionCount
        n
      }
    }
    val gasPriceValue = gasPrice match {
      case Some(o) => o
      case None => {
        val n: BigInt = client.ethGasPrice().send.getGasPrice
        n
      }
    }
    val gasLimitValue: BigInt = gasLimit match {
      case Some(o) => o
      case None => {
        val tx =
          Transaction.createEthCallTransaction(
            account.address.toString,
            to.toString,
            inputString
          )
        val estimateResult =
          client
            .ethEstimateGas(tx)
            .send
        if (estimateResult.hasError) {
          throw Exception(
            "estimate gas err:" + estimateResult.getError.getMessage
          )
        }
        estimateResult.getAmountUsed
      }
    }
    val b = block match {
      case Some(o) =>
        DefaultBlockParameterNumber(BigInteger.valueOf(o.longValue))
      case None => DefaultBlockParameterName.LATEST
    }


    val result = client
      .ethCall(
        Transaction.createFunctionCallTransaction(
          account.address.toString,
          nonceValue.bigInteger,
          gasPriceValue.bigInteger,
          // BigInt(0).bigInteger,
          gasLimitValue.bigInteger,
          to.toString,
          value.bigInteger,
          inputString
        ),
        b
      )
      .send
    if(result.isReverted) {
      throw Exception("call reverted:" + result.getRevertReason)
    }
    function.unpackOutput(result.getValue.toBytes.!).toTry
  }
}

class Web3Client(url: String) {
  val http = new HttpService(url)
  val client = Web3j.build(http)
  val chainId: BigInt = client.ethChainId.send.getChainId

}
