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
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.ClientTransactionManager

class Web3Client(
    val client:      Web3j,
    val fromAddress: Address
) {
    val txManager        = ReadonlyTransactionManager(client, fromAddress.toHex)
    protected val logger = Logger("transaction")

    /** 读取合约数据
      */
    def read[IN, OUT, T](function: ABIFunction[IN, OUT])(
        to: Address,
        params: T,
        block: Option[BigInt] = None
    )(using converter: ConvertFromScala[T, IN]): Try[OUT] = {
        Try {
            val input       = converter.fromScala(params).!
            val inputString = function.packInputWithSelector(params).toHex()
            val b           = block match {
                case Some(o) =>
                    DefaultBlockParameterNumber(BigInteger.valueOf(o.longValue))
                case None    => DefaultBlockParameterName.LATEST
            }
            val result      = txManager.sendCall(to.toHex, inputString, b)
            function.unpackOutput(result.toBytes.!).toOption.get
        }
    }

    def getPendingNonce: BigInt = {
        val n: BigInt = client
            .ethGetTransactionCount(
              fromAddress.toString,
              DefaultBlockParameterName.PENDING
            )
            .send
            .getTransactionCount
        n

    }
    def gasPrice = {
        val fromNode: BigInt = client.ethGasPrice.send.getGasPrice
        fromNode

    }

    def estimateGas(
        from: Address,
        to: Address,
        nonce: BigInt,
        value: BigInt,
        input: Array[Byte]
    ): BigInt = {
        val callData =
            Transaction.createFunctionCallTransaction(
              fromAddress.toString,
              nonce.bigInteger,
              null,
              null,
              to.toString,
              value.bigInteger,
              input.toHex()
            )

        val estimateResult =
            client
                .ethEstimateGas(callData)
                .send
        if (estimateResult.hasError) {
            throw Exception(
              "estimate gas err:" + estimateResult.getError.getMessage
            )
        }
        val n: BigInt      = estimateResult.getAmountUsed
        n
    }

    /** 模拟执行
      */
    def call[IN, OUT, T](function: ABIFunction[IN, OUT])(
        to: Address,
        params: T,
        nonce: Option[BigInt] = None,
        gasPrice: Option[BigInt] = None,
        gasLimit: Option[BigInt] = None,
        value: BigInt = 0,
        block: Option[BigInt] = None
    )(using converter: ConvertFromScala[T, IN]): Try[OUT] = {
        Try {
            val input = function.selector ++ function.packInput(params)

            val b             = block match {
                case Some(o) =>
                    DefaultBlockParameterNumber(BigInteger.valueOf(o.longValue))
                case None    => DefaultBlockParameterName.LATEST
            }
            val nonceValue    = nonce match {
                case Some(o) => o
                case None    => getPendingNonce
            }
            val gasPriceValue = gasPrice match {
                case Some(o) => o
                case None    => {
                    val n: BigInt = client.ethGasPrice().send.getGasPrice
                    n
                }
            }

            val gasLimitValue = gasLimit match {
                case Some(o) => o
                case None    => estimateGas(fromAddress, to, nonceValue, value, input)
            }

            val tx = Transaction.createFunctionCallTransaction(
              fromAddress.toHex,
              nonceValue.bigInteger,
              gasPriceValue.bigInteger,
              gasLimitValue.bigInteger,
              to.toString,
              value.bigInteger,
              input.toHex()
            )

            val sendResult = client.ethCall(tx, b).send
            logger.info("tx hash:" + sendResult.getResult)
            if (sendResult.isReverted) {
                throw Exception("execution reverted:" + sendResult.getRevertReason)
            }
            function.unpackOutput(sendResult.getResult.toBytes.!).!
        }
    }
}
