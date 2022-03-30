package io.github.liewhite.web3.contract

import io.github.liewhite.web3.types.Address
import io.github.liewhite.json.codec.Encoder
import scala.annotation.tailrec

// 必须要在调用处存在 两者类型的 typeclass才允许调用
trait ConvertFromScala[-S, +A] {
  // todo 返回 Either[Exception, A]
  def fromScala(s: S): Either[Exception, A]
}

object ConvertFromScala {
  given ConvertFromScala[EmptyTuple, EmptyTuple] with {
    def fromScala(t: EmptyTuple) = Right(t)
  }

  given [H, T <: Tuple, ABIH, ABIT <: Tuple](using
      headConverter: => ConvertFromScala[H, ABIH],
      tailConverter: => ConvertFromScala[T, ABIT]
  ): ConvertFromScala[H *: T, ABIH *: ABIT] with {
    def fromScala(value: H *: T) = {
      value match {
        case (h *: t) => {
          val hResult = headConverter.fromScala(h)
          hResult match {
            case Right(hSucc) => {
              tailConverter.fromScala(t) match {
                case Right(tSucc) => {
                  Right(hSucc *: tSucc)
                }
                case Left(e) => Left(e)
              }
            }
            case Left(e) => Left(e)
          }
        }
      }
    }
  }

  given [A, B]: ConvertFromScala[A, B] = new ConvertFromScala[A, B] {
    def fromScala(value: A): Either[Exception, B] = {
      ???
    }
  }

  def convertSeq[A, B](
      s: Seq[A]
  )(using converter: ConvertFromScala[A, B]): Either[Exception, Vector[B]] = {
    convertSeqIter(s, Right(Vector.empty[B]))
  }

  @tailrec
  private def convertSeqIter[A, B](
      s: Seq[A],
      acc: Either[Exception, Vector[B]]
  )(using converter: ConvertFromScala[A, B]): Either[Exception, Vector[B]] = {
    if (s.isEmpty) {
      acc
    } else {
      val v2 = converter.fromScala(s.head)
      v2 match {
        case Right(o) => convertSeqIter(s.tail, acc.map(_.appended(o)))
        case Left(e)  => Left(e)
      }
    }
  }
}
