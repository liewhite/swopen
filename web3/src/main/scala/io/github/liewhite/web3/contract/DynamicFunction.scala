// package io.github.liewhite.web3.contract

// import scala.language.postfixOps
// import org.web3j.abi.datatypes.generated.Uint8
// import org.web3j.abi.datatypes.Bool
// import io.github.liewhite.common.SummonUtils
// import io.github.liewhite.web3.types.Address
// import io.github.liewhite.web3.Extensions.*
// import io.github.liewhite.web3.contract.types.ABIAddress

// import io.github.liewhite.web3.common.ConvertFromScala

// enum ABIItem {
//   case Function(name:String, inputs: Vector[])
// }
// // a contract function with static type
// // IN is Tuple of ABITypes, OUT is ABITypes
// // there is a nested typeclass, IN context bound to a ABIDecoder, OUT context bound to a ABIDecoder
// // then, encoder/decoder has encode/decode method, it takes paramter()
// // named with case class and unamed with Tuple
// class DynamicABIFunction(
//     val functionAbi: String
// ){
//   def functionSignature: String = name + inPack.typeName
//   def selector: Array[Byte] = {
//     val fsignature = name + inPack.typeName
//     org.web3j.crypto.Hash.sha3(fsignature.getBytes).slice(0, 4)
//   }
//   override def toString: String = functionSignature

//   def packInput[T](
//       args: T
//   )(using converter: ConvertFromScala[T, IN]): Array[Byte] = {
//     inPack.pack(converter.fromScala(args))
//   }

//   def packInputWithSelector[T](
//       args: T
//   )(using converter: ConvertFromScala[T, IN]): Array[Byte] = {
//     selector ++ inPack.pack(converter.fromScala(args))
//   }

//   def unpackInput(args: Array[Byte]): IN = {
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
//     outPack.pack(converter.fromScala(args))
//   }

//   def unpackOutput(args: Array[Byte]): OUT = {
//     outPack.unpack(args)
//   }

// }
