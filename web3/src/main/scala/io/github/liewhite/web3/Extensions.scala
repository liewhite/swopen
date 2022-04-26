package io.github.liewhite.web3

import io.github.liewhite.common
import org.apache.commons.codec.binary.Hex

object Extensions extends common.CommonExtensions {
    extension (s: String) {
        def hexToBytes: Array[Byte] = {
            val without0x  = if (s.startsWith("0x")) s.slice(2, s.length) else s
            // 差一位自动补位
            val evenLength = if (without0x.length % 2 == 0) without0x else "0" + without0x
            Hex.decodeHex(evenLength)
        }
    }

    extension (bs: Array[Byte]) {
        def toHex(withPrefix: Boolean = true): String = {
            val prefix = if (withPrefix) "0x" else ""
            prefix + Hex.encodeHex(bs).mkString
        }

        // bytes to Uint
        def toBigUint: BigInt = {
            // pad sign 0 manually
            BigInt(Array[Byte](0) ++ bs)
        }

        def toBigInt: BigInt = {
            BigInt(bs)
        }
    }

    // 32 bytes length
    extension (i: BigInt) {
        def toUintByte32: Array[Byte] = {
            if (i < 0) {
                throw java.lang.IllegalArgumentException("negative to uint error: " + i)
            }
            val rawBytes = i.toByteArray.dropWhile(_ == 0)
            if (rawBytes.length > 32) {
                throw java.lang.IllegalArgumentException("too large number for Byte32" + i)
            }
            Array.fill(32 - rawBytes.length)(0.toByte) ++ rawBytes
        }

        def toIntByte32: Array[Byte] = {
            val rawBytes    = i.toByteArray
            if (rawBytes.length > 32) {
                throw java.lang.IllegalArgumentException("too large number for Byte32" + i)
            }
            val paddingByte = (if (i < 0) 0xff else 0).toByte
            val paddingLen  = 32 - rawBytes.length
            val paddings    = Array.fill(paddingLen)(paddingByte)
            paddings ++ rawBytes
        }

    }

}
