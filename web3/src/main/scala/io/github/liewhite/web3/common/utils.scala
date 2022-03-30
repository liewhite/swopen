package io.github.liewhite.web3.common

import scala.annotation.tailrec


def unliftEither[T](
    s: Seq[Either[Exception,T]],
): Either[Exception, Seq[T]] = {
    unliftEitherIter(s, Right(Seq.empty[T]))
}

@tailrec
private def unliftEitherIter[T](
    s: Seq[Either[Exception,T]],
    acc: Either[Exception, Seq[T]]
): Either[Exception, Seq[T]] = {
  if (s.isEmpty) {
    acc
  } else {
    s.head match {
      case Right(o) => unliftEitherIter(s.tail,acc.map(_.appended(o)))
      case Left(e) => Left(e)
    }
  }
}