package io.github.liewhite.web3.contract

import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.abi.datatypes.Bool
import io.github.liewhite.common.SummonUtils
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.types.ABIAddress

import io.github.liewhite.web3.common.ConvertFromScala

// a contract function with static type
// IN is Tuple of ABITypes, OUT is ABITypes
// there is a nested typeclass, IN context bound to a ABIDecoder, OUT context bound to a ABIDecoder
// then, encoder/decoder has encode/decode method, it takes paramter()
// named with case class and unamed with Tuple
class ABIFunction[IN, OUT](
    val name: String
)(using inPack: ABIPack[IN], outPack: ABIPack[OUT]) {
  def apply[T,ADDR](contract: ADDR, input: T)(using
      converter: ConvertFromScala[T, IN],
      addrConverter: ConvertFromScala[ADDR, ABIAddress]
  ): Either[Exception, OUT] = {
    val addr = addrConverter.fromScala(contract)
    // call contract on chain
    ???
  }

  def functionSignature:String = name + inPack.typeName
  def selector: Array[Byte] = {
    val fsignature = name + inPack.typeName
    org.web3j.crypto.Hash.sha3(fsignature.getBytes).slice(0, 4)
  }
  override def toString: String = functionSignature

  def packInput[T](args: T)(using converter: ConvertFromScala[T, IN]): Array[Byte] = {
    inPack.pack(converter.fromScala(args).toOption.get)
  }

  def unpackInput(args: Array[Byte]): Either[Exception,IN]  = {
    inPack.unpack(args)
  }

  def packOutput[T](args: T)(using converter: ConvertFromScala[T, OUT]): Array[Byte] = {
    outPack.pack(converter.fromScala(args).toOption.get)
  }

  def unpackOutput(args: Array[Byte]): Either[Exception,OUT]  = {
    outPack.unpack(args)
  }

}
