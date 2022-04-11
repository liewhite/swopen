package io.github.liewhite.web3

import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.types.BytesType
import io.github.liewhite.json.JsonExtensions
import io.github.liewhite.common
import org.apache.commons.codec.binary.Hex

object Extensions extends common.CommonExtensions with JsonExtensions {
    extension (s: String) {
        def toAddress: Either[Exception, Address]   = BytesType.fromString[Address](s)
        def toBytes: Either[Exception, Array[Byte]] = {
            val without0x  = if (s.startsWith("0x")) s.slice(2, s.length) else s
            val evenLength = if (without0x.length % 2 == 0) without0x else "0" + without0x

            try {
                Right(Hex.decodeHex(evenLength))
            } catch {
                case e: Exception => Left(e)
            }
        }
    }

    extension (bs: Array[Byte]) {
        def toAddress: Either[Exception, Address]     = BytesType.fromBytes[Address](bs)
        def toHex(withPrefix: Boolean = true): String = {
            val prefix = if (withPrefix) "0x" else ""
            prefix + Hex.encodeHex(bs).mkString
        }
        // bytes to Uint
        def toBigUint: Option[BigInt]                 = {
            if (bs.length == 0) {
                None
            }
            // pad sign 0 manually
            Some(BigInt(Array[Byte](0) ++ bs))
        }

        def toBigInt: Option[BigInt] = {
            try {
                Some(BigInt(bs))
            } catch {
                case _ => None

            }
        }
    }

    // 32 bytes length
    extension (i: BigInt) {
        def toUintByte32: Option[Array[Byte]] = {
            if (i < 0) {
                return None
            }
            val rawBytes = i.toByteArray.dropWhile(_ == 0)
            if (rawBytes.length > 32) {
                return None
            }
            Some(Array.fill(32 - rawBytes.length)(0.toByte) ++ rawBytes)
        }

        def toIntByte32: Option[Array[Byte]] = {
            val rawBytes    = i.toByteArray
            if (rawBytes.length > 32) {
                return None
            }
            val paddingByte = (if (i < 0) 0xff else 0).toByte
            val paddingLen  = 32 - rawBytes.length
            val paddings    = Array.fill(paddingLen)(paddingByte)
            Some(paddings ++ rawBytes)
        }

    }

}
