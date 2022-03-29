package io.github.liewhite.web3.contract
import scala.compiletime.constValue


trait ABIPack[T] {

  def dynamic: Boolean
  // 创建时确保正确， 此处不返回错误
  def pack(t:T): Array[Byte]

  def unpack(bytes: Array[Byte]): Either[Exception,T]

}

object ABIPack {
  def alignTo32(bytes: Array[Byte], direction: "left" | "right", length: Int = 32): Array[Byte] = {
    if(direction == "left"){
      bytes.reverse.padTo(length,0.toByte).reverse
    }else{
      bytes.padTo(length,0.toByte)
    }
  }

  def alignBytes(bytes: Array[Byte]): Array[Byte] = {
    def iter(acc: Array[Byte], rest: Array[Byte]): Array[Byte] = {
      val len = rest.length
      if (len == 0) {
        acc
      } else if (len < 32) {
        acc ++ ABIPack.alignTo32(rest, "right")
      } else {
        iter(acc ++ rest.slice(0, 32), rest.slice(32, len))
      }
    }
    iter(Array.emptyByteArray, bytes)
  }
}
