package io.github.liewhite.web3.contract.types

import io.github.liewhite.web3.contract.ConvertFromScala
import scala.annotation.tailrec

def convertSeq[A, B](
    s: Seq[A],
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