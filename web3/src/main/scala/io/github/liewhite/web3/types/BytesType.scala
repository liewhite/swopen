package io.github.liewhite.web3.types

import io.github.liewhite.web3.Extensions.*

abstract class BytesType(val bytes: Array[Byte], length: Int) {
    if (bytes.length != length) {
        throw IllegalArgumentException("bytes length must be " + bytes.length)
    }
    def toHex: String = {
        bytes.toHex()
    }

    override def toString: String = {
        bytes.toHex()
    }

    def lowerCaseString: String = {
        toString.toLowerCase
    }

    override def equals(that: Any): Boolean = bytes.sameElements(that.asInstanceOf[Address].bytes)

    override def hashCode: Int = {
        bytes.toHex().hashCode
    }
}
