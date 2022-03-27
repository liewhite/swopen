package io.github.liewhite.web3.contract

import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.abi.datatypes.Bool
import io.github.liewhite.web3.contract.codec.ABIEncoder
import io.github.liewhite.common.SummonUtils

// a contract function with static type
// IN is Tuple of ABITypes, OUT is ABITypes
// there is a nested typeclass, IN context bound to a ABIDecoder, OUT context bound to a ABIDecoder
// then, encoder/decoder has encode/decode method, it takes paramter()
class ABIFunction[IN <: Tuple , OUT](val name:String){
    def apply[T](input: T)(using converter: ConvertFromScala[T,IN]): OUT = {
        println(input)
        println(converter.fromScala(input))
        // call contract on chain
        ???
    }
    def pack(args: IN): Array[Byte] = {
        ???
    }
}