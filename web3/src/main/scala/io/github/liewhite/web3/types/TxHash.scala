package io.github.liewhite.web3.types

import scala.math.BigInt
import io.github.liewhite.web3.Extensions.*

class TxHash(bytes: Array[Byte]) extends BytesType(bytes,32,false) {
    def this(hex: String) = {
        this(hex.hexToBytes)
    }
}
