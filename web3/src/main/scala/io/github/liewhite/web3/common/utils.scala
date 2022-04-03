package io.github.liewhite.web3.common

import scala.annotation.tailrec
import io.github.liewhite.web3.types.Address
import io.github.liewhite.web3.contract.types.ABIAddress

def alignLength(length: Int, align: Int = 32): Int = {
  if (length % align == 0 || length == 0 ) {
      length
    } else {
      math.ceil(length.toDouble / align).toInt * align
    }
}

def padLeftZero(bytes: Array[Byte]):Array[Byte] = {
  new Array[Byte](32 - bytes.length) ++ bytes
}

def padAddress(i: ABIAddress): Array[Byte] = {
  val bytes = i.value.bytes
  new Array[Byte](32 - bytes.length) ++ bytes
}

// padding 0 at right and align to 32 bytes
def padBytes(bytes: Array[Byte]): Array[Byte] = {
  def iter(acc: Array[Byte], rest: Array[Byte]): Array[Byte] = {
    val len = rest.length
    if (len == 0) {
      acc
    } else if (len < 32) {
      acc ++ rest ++ Array.fill(32 - len)(0.toByte)
    } else {
      iter(acc ++ rest.slice(0, 32), rest.slice(32, len))
    }
  }
  iter(Array.emptyByteArray, bytes)
}

// padding 0 at right and align to 32 bytes
def padString(str: String): Array[Byte] = {
  padBytes(str.getBytes)
}

def unliftEither[T](
    s: Seq[Either[Exception, T]]
): Either[Exception, Seq[T]] = {
  unliftEitherIter(s, Right(Seq.empty[T]))
}

@tailrec
private def unliftEitherIter[T](
    s: Seq[Either[Exception, T]],
    acc: Either[Exception, Seq[T]]
): Either[Exception, Seq[T]] = {
  if (s.isEmpty) {
    acc
  } else {
    s.head match {
      case Right(o) => unliftEitherIter(s.tail, acc.map(_.appended(o)))
      case Left(e)  => Left(e)
    }
  }
}
