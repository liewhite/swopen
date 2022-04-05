package io.github.liewhite.web3.contract

import scala.language.postfixOps
import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.abi.datatypes.Bool
import io.github.liewhite.common.SummonUtils
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.contract.types.ABIAddress

import io.github.liewhite.web3.common.ConvertFromScala
import org.web3j.protocol.Web3j
import io.github.liewhite.web3.wallet.Bip44Wallet
import io.github.liewhite.web3.rpc.Web3ClientWithCredential
import org.web3j.crypto.RawTransaction

// a contract function with static type
// IN is Tuple of ABITypes, OUT is ABITypes
// there is a nested typeclass, IN context bound to a ABIDecoder, OUT context bound to a ABIDecoder
// then, encoder/decoder has encode/decode method, it takes paramter()
// named with case class and unamed with Tuple
class ABIFunction[IN, OUT](
    val name: String
)(using inPack: ABIPack[IN], outPack: ABIPack[OUT]) {

  def apply[T](
      contract: Address,
      input: T,
      nonce: Option[BigInt] = None,
      gasPrice: Option[BigInt] = None,
      gasLimit: Option[BigInt] = None,
      baseFee: Option[BigInt] = None,
      maxPriorityFee: Option[BigInt] = None,
      value: BigInt = 0,
      dryRun: Boolean = false,
      blockNum: Option[BigInt] = None
  )(
      client: Web3ClientWithCredential
  )(using
      converter: ConvertFromScala[T, IN]
  ): Either[Exception, OUT] = {
    val data = converter.fromScala(input)
    val d = data.!
    // val rawTransaction = RawTransaction.createTransaction()
    // client.rawTransactionManger.signAndSend()
    // call contract on chain
    // client.ethCall()
    ???
  }

  def functionSignature: String = name + inPack.typeName
  def selector: Array[Byte] = {
    val fsignature = name + inPack.typeName
    org.web3j.crypto.Hash.sha3(fsignature.getBytes).slice(0, 4)
  }
  override def toString: String = functionSignature

  def packInput[T](
      args: T
  )(using converter: ConvertFromScala[T, IN]): Array[Byte] = {
    inPack.pack(converter.fromScala(args).toOption.get)
  }

  def packInputWithSelector[T](
      args: T
  )(using converter: ConvertFromScala[T, IN]): Array[Byte] = {
    selector ++ inPack.pack(converter.fromScala(args).toOption.get)
  }

  def unpackInput(args: Array[Byte]): Either[Exception, IN] = {
    val params = if (args.startsWith(selector)) {
      args.drop(4)
    } else {
      args
    }
    inPack.unpack(params)
  }

  def packOutput[T](
      args: T
  )(using converter: ConvertFromScala[T, OUT]): Array[Byte] = {
    outPack.pack(converter.fromScala(args).toOption.get)
  }

  def unpackOutput(args: Array[Byte]): Either[Exception, OUT] = {
    outPack.unpack(args)
  }

}
