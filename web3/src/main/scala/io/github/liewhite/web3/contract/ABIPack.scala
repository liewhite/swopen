package io.github.liewhite.web3.contract
import scala.compiletime.constValue
import io.github.liewhite.common.SummonUtils
import io.github.liewhite.web3.common.unliftEither
import scala.math

trait ABIPack[T] {

  def dynamic: Boolean

  // 静态部分字节数, 出了静态数组和tuple， 绝大部分都是32
  def length: Int

  // 创建时确保正确， 此处不返回错误
  def pack(t: T): Array[Byte]

  def unpack(bytes: Array[Byte]): Either[Exception, T]

}

object ABIPack {

  def alignLength(
      length: Int,
      align: Int = 32
  ): Int = {
    if(length % align == 0 ) {
      length
    } else{
      math.ceil(length.toDouble / align).toInt * align
    }
  }

  def alignTo32(
      bytes: Array[Byte],
      direction: "left" | "right",
      length: Int = 32
  ): Array[Byte] = {
    if (direction == "left") {
      bytes.reverse.padTo(length, 0.toByte).reverse
    } else {
      bytes.padTo(length, 0.toByte)
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

  inline given [T <: Tuple]: ABIPack[T] = new ABIPack[T] {
    val packs = SummonUtils.summonAll[ABIPack, T]

    def dynamic: Boolean = !packs.forall(!_.dynamic)

    // 静态部分字节数
    def length: Int = if (dynamic) 32 else packs.map(_.length).sum

    def pack(t: T): Array[Byte] = {
      val staticPart = Array.emptyByteArray
      val dynamicPart = Array.emptyByteArray
      // 动态部分开始位置
      val dynamicOffset = packs.map(_.length).sum

      val elems = packs.zip(t.toArray.toVector).map { case (p, i) =>
        p.pack(i)
      }
      val result = packs
        .zip(elems)
        .foldLeft((staticPart, dynamicPart, dynamicOffset))((acc, item) => {
          if (!item._1.dynamic) {
            // 静态类型直接拼接到static部分
            (acc._1 ++ item._2, acc._2, 0)
          } else {
            // 动态类型在静态部分保留offset
            val offsetBytes =
              ABIPack.alignTo32(BigInt(acc._3).toByteArray, "left")
            (
              acc._1 ++ offsetBytes,
              acc._2 ++ item._2,
              acc._3 + item._2.length
            )
          }
        })
      result._1 ++ result._2
    }

    def unpack(bytes: Array[Byte]): Either[Exception, T] = {
      // 计算出静态部分长度， 即动态部分的offset, 然后从静态部分开始依次decode
      val staticOffset = 0

      val result = packs.foldLeft(
        (List.empty[Either[Exception, Any]], staticOffset)
      )((acc, item) => {
        if (!item.dynamic) {
          val data =
            item.unpack(bytes.slice(acc._2, acc._2 + item.length))
          (
            acc._1.appended(data),
            // 对齐到32字节
            acc._2 + alignLength(item.length)
          )
        } else {
          val dynamicOffset = BigInt(bytes.slice(acc._2, acc._2 + 32)).toInt
          val dynamicBytes = bytes.slice(dynamicOffset, bytes.length)
          val unpacked = item.unpack(dynamicBytes)
          (
            acc._1.appended(unpacked),
            acc._2 + 32
          )
        }
      })
      val unlifted = unliftEither(result._1)
      unlifted.map(item => {
          Tuple.fromArray(item.toArray).asInstanceOf[T]
      })
    }
  }

  // given ABIPack[EmptyTuple] with {
  //   def length = 0

  //   def dynamic: Boolean = false

  //   def pack(t: EmptyTuple): Array[Byte] = Array.emptyByteArray

  //   def unpack(bytes: Array[Byte]): Either[Exception, EmptyTuple] = Right(
  //     EmptyTuple
  //   )
  // }
  // given [H, T <: Tuple](using
  //     headPack: => ABIPack[H],
  //     tailPack: => ABIPack[T]
  // ): ABIPack[H *: T] with {

  //   def length = {
  //     headPack.length + tailPack.length
  //   }

  //   def dynamic: Boolean = tailPack.dynamic || tailPack.dynamic

  //   // 自身静态放最前面, 动态部分放tail的静态部分后面
  //   // 如果tail是静态， 直接拼接， 如果是动态， 则
  //   // 从tail中取静态部分拼接在后，
  //   def pack(t: EmptyTuple): Array[Byte] = {
  //     var bytesBuffer = Array.emptyByteArray
  //     if(!headPack.dynamic) {

  //     }
  //   }

  //   def unpack(bytes: Array[Byte]): Either[Exception, H *: T] = {
  //     ???
  //   }
  // }
}
