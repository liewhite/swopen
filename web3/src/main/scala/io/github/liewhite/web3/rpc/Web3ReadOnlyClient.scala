package io.github.liewhite.web3.rpc

import scala.language.postfixOps
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.ABIFunction
import io.github.liewhite.web3.contract.ABIPack
import io.github.liewhite.web3.common.EthValue
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import scala.util.Try
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.types.TxHash
import io.github.liewhite.web3.common.ConvertFromScala
import org.web3j.protocol.core.DefaultBlockParameterNumber
import scala.concurrent.duration.Duration
import org.web3j.protocol.core.methods.response.TransactionReceipt
import com.typesafe.scalalogging.Logger
import java.math.BigInteger
import org.web3j.tx.ReadonlyTransactionManager

class Web3ReadOnlyClient(
    val client:      Web3j,
) {
    val txManager        = ReadonlyTransactionManager(client, "0x0000000000000000000000000000000000000000")
    protected val logger = Logger("transaction")

    // def this()
    /** 读取合约数据
      */
    def read[IN, OUT, T](function: ABIFunction[IN, OUT])(
        to: Address,
        params: T,
        block: Option[BigInt] = None
    )(using converter: ConvertFromScala[T, IN]): Try[OUT] = {
        Try {
            val input       = converter.fromScala(params)
            val inputString = function.packInputWithSelector(params).toHex()
            val b           = block match {
                case Some(o) =>
                    DefaultBlockParameterNumber(BigInteger.valueOf(o.longValue))
                case None    => DefaultBlockParameterName.LATEST
            }
            val result      = txManager.sendCall(to.toHex, inputString, b)
            function.unpackOutput(result.hexToBytes)
        }
    }

}
