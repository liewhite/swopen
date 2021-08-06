package io.github.liewhite.json.utils

import scala.compiletime.*
import scala.reflect.ClassTag

object SummonUtils:
  type Head[T] = T match { case h *: t => h }
  type Tail[T] = T match { case h *: t => t }
  type LiftP[F[_], T] <: Tuple =
    T match {
      case _ *: _ => F[Head[T]] *: LiftP[F, Tail[T]]
      case _ => EmptyTuple
    }
  inline def summonAll[F[_], T <: Tuple]: List[F[Any]] = 
    inline erasedValue[T] match
        case _: (t *: ts) => summonInline[F[t]].asInstanceOf[F[Any]] :: summonAll[F,ts]
        case _: EmptyTuple => Nil


  inline def summonValuesAsArray[T <: Tuple, E: ClassTag]: Array[E] =
    summonValuesAsArray0[T, E](0, new Array[E](constValue[Tuple.Size[T]]))

  inline def summonValuesAsArray0[T, E](i: Int, arr: Array[E]): Array[E] = inline erasedValue[T] match {
    case _: EmptyTuple => arr
    case _: (a *: b) =>
      arr(i) = constValue[a & E]
      summonValuesAsArray0[b, E](i+1, arr)
  }
