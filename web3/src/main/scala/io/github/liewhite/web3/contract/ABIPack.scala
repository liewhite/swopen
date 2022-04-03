package io.github.liewhite.web3.contract
import scala.compiletime.constValue
import io.github.liewhite.common.SummonUtils
import io.github.liewhite.web3.Extensions.*
import io.github.liewhite.web3.common
import scala.math

trait ABIPack[T] {

  def dynamic: Boolean

  // 静态部分字节数, 出了静态数组和tuple， 绝大部分都是32
  def staticSize: Int

  def typeName: String

  // 创建时确保正确， 此处不返回错误
  def pack(t: T): Array[Byte]

  def unpack(bytes: Array[Byte]): Either[Exception, T]

}

object ABIPack {
  inline given [T <: Tuple]: ABIPack[T] = new ABIPack[T] {
    val packs = SummonUtils.summonAll[ABIPack, T]
    def typeName: String = "(" + packs.map(_.typeName).mkString(",") + ")"

    def dynamic: Boolean = !packs.forall(!_.dynamic)

    // 静态部分字节数
    def staticSize: Int = if (dynamic) 32 else packs.map(_.staticSize).sum

    def pack(t: T): Array[Byte] = {
      val staticPart = Array.emptyByteArray
      val dynamicPart = Array.emptyByteArray
      // 动态部分开始位置
      val dynamicOffset = packs.map(_.staticSize).sum

      val elems = packs.zip(t.toArray.toVector).map { case (p, i) =>
        p.pack(i)
      }
      val result = packs
        .zip(elems)
        .foldLeft((staticPart, dynamicPart, dynamicOffset))((acc, item) => {
          if (!item._1.dynamic) {
            // 静态类型直接拼接到static部分
            (acc._1 ++ item._2, acc._2, dynamicOffset)
          } else {
            // 动态类型在静态部分保留offset
            val offsetBytes = BigInt(acc._3).toUintByte32.get
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
            item.unpack(bytes.slice(acc._2, acc._2 + item.staticSize))
          (
            acc._1.appended(data),
            // 对齐到32字节
            acc._2 + common.alignLength(item.staticSize)
          )
        } else {
          val dynamicOffsetOption = bytes.slice(acc._2, acc._2 + 32).toBigUint
          dynamicOffsetOption match {
            case Some(dynamicOffset) => {
              val dynamicBytes = bytes.slice(dynamicOffset.toInt, bytes.length)
              val unpacked = item.unpack(dynamicBytes)
              (
                acc._1.appended(unpacked),
                acc._2 + 32
              )
            }
            case None => (
                acc._1.appended(Left(Exception("failed parse dynamicOffset"))),
                acc._2 + 32
            )
          }
        }
      })
      val unlifted = common.unliftEither(result._1)
      unlifted.map(item => {
        Tuple.fromArray(item.toArray).asInstanceOf[T]
      })
    }
  }
}
