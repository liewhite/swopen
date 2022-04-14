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

class Web3ClientWithCredential(
    client:        Web3j,
    val account:   Account,
    retries:       Int = 20,
    sleepDuration: Int = 15 * 1000
) extends Web3Client(client, account.getAddress) {
    val rawTxManager = RawTransactionManager(
      client,
      account.toCredential,
      client.ethChainId.send.getChainId.longValue,
      retries,
      sleepDuration
    )

    def transfer(to: Address, value: BigDecimal, gasPriceFactor: Double = 1): Try[TxHash] = {
        val t = new Transfer(client, rawTxManager).sendFunds(
          to.toString,
          value.bigDecimal,
          Convert.Unit.WEI,
          (BigDecimal(gasPrice) * gasPriceFactor).toBigInt.bigInteger,
          BigInteger.valueOf(21000)
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
        gasPriceFactor: Double = 1,
        gasLimit: Option[BigInt] = None,
        value: BigInt = 0
    )(using converter: ConvertFromScala[T, IN]): Try[TransactionReceipt] = {
        Try {
            val input = function.selector ++ function.packInput(params)

            val nonceValue    = nonce match {
                case Some(o) => o
                case None    => getPendingNonce
            }
            val gasPriceValue = gasPrice match {
                case Some(o) => o.bigInteger
                case None    => client.ethGasPrice().send.getGasPrice
            }
            val gasLimitValue = gasLimit match {
                case Some(o) => o
                case None    => estimateGas(account.getAddress, to, nonceValue, value, input)
            }

            val tx = RawTransaction.createTransaction(
              nonceValue.bigInteger,
              (BigDecimal(gasPriceValue) * gasPriceFactor).toBigInt.bigInteger,
              gasLimitValue.bigInteger,
              to.toString,
              value.bigInteger,
              input.toHex()
            )

            val sendResult = rawTxManager.signAndSend(tx)
            if (sendResult.hasError) {
                throw Exception(sendResult.getError.getMessage)
            }
            val hash       = sendResult.getTransactionHash
            logger.info("tx hash:" + sendResult.getTransactionHash)

            def pollReceipt(hash: String, retried: Int): TransactionReceipt = {
                val r = client.ethGetTransactionReceipt(hash).send.getTransactionReceipt
                if (r.isPresent) {
                    r.get
                } else {
                    if (retried > retries) {
                        throw Exception("wait for receipt timeout: " + hash)
                    }
                    Thread.sleep(sleepDuration)
                    pollReceipt(hash, retried + 1)
                }
            }

            pollReceipt(hash, 0)
        }
    }
}
