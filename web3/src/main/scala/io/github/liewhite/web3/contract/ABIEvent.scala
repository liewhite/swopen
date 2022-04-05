// package io.github.liewhite.web3.contract

// import scala.language.postfixOps
// import scala.compiletime.constValue
// import org.web3j.abi.datatypes.generated.Uint8
// import org.web3j.abi.datatypes.Bool
// import io.github.liewhite.common.SummonUtils
// import io.github.liewhite.web3.types.Address
// import io.github.liewhite.web3.Extensions.*
// import io.github.liewhite.web3.contract.types.ABIAddress

// import io.github.liewhite.web3.common.ConvertFromScala
// import org.web3j.protocol.Web3j
// import io.github.liewhite.web3.wallet.Bip44Wallet
// import io.github.liewhite.web3.rpc.Web3ClientWithCredential
// import org.web3j.crypto.RawTransaction


// case class EventParam[T: ABIPack, Indexed <: Boolean](t: T, indexed: Boolean)

// object EventParam {
//   inline given[T, Indexed <: Boolean](using t: ABIPack[T]): ABIPack[EventParam[T, Indexed]] = 
//     new ABIPack[EventParam[T, Indexed]] {
//       def indexed: Boolean = constValue[Indexed]

//       def typeName: String = t.typeName

//       def dynamic: Boolean = t.dynamic

//       def staticSize: Int = t.staticSize

//       def pack(param: EventParam[T, Indexed]): Array[Byte] = t.pack(param.t)
//       def unpack(bytes: Array[Byte]): Either[Exception, EventParam[T, Indexed]] = {
//         t.unpack(bytes).map(item => EventParam(item,indexed))
//       }
//     }
// }

// class ABIEvent[T <: Tuple](
//     val name: String,
//     val anonymouns: Boolean = false
// )(using indexedPack: ABIPack[INDEXED], unindexedPack: ABIPack[UNINDEXED]) {
//   def eventSignature: String = name + .typeName

//   def topic0: Array[Byte] = {
//     val fsignature = name + pack.typeName
//     org.web3j.crypto.Hash.sha3(fsignature.getBytes).slice(0, 4)
//   }
//   override def toString: String = eventSignature

//   def packInput[T](
//       args: T
//   )(using converter: ConvertFromScala[T, IN]): Array[Byte] = {
//     inPack.pack(converter.fromScala(args).toOption.get)
//   }

//   def packInputWithSelector[T](
//       args: T
//   )(using converter: ConvertFromScala[T, IN]): Array[Byte] = {
//     selector ++ inPack.pack(converter.fromScala(args).toOption.get)
//   }

//   def unpackInput(args: Array[Byte]): Either[Exception, IN] = {
//     val params = if (args.startsWith(selector)) {
//       args.drop(4)
//     } else {
//       args
//     }
//     inPack.unpack(params)
//   }

//   def packOutput[T](
//       args: T
//   )(using converter: ConvertFromScala[T, OUT]): Array[Byte] = {
//     outPack.pack(converter.fromScala(args).toOption.get)
//   }

//   def unpackOutput(args: Array[Byte]): Either[Exception, OUT] = {
//     outPack.unpack(args)
//   }

// }
