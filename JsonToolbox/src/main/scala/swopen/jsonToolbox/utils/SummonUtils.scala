package swopen.jsonToolbox.utils

import scala.compiletime.{erasedValue,summonInline}
import shapeless3.deriving.*

object SummonUtils:
  inline def summonAll[F[_], T <: Tuple]: List[F[Any]] = 
    inline erasedValue[T] match
        case _: (t *: ts) => summonInline[F[t]].asInstanceOf[F[Any]] :: summonAll[F,ts]
        case _: EmptyTuple => Nil