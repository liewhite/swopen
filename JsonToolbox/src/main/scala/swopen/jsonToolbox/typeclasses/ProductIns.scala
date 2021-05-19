package swopen.jsonToolbox.typeclasses

import scala.deriving.*
import swopen.jsonToolbox.utils.SummonUtils.*

trait ProductInst[T[_],A]:
  def elemT: List[T[Any]]

object ProductInst:
  inline given [T[_],A](using mirror: Mirror.ProductOf[A]): ProductInst[T,A] = new ProductInst[T,A]{
    def elemT = summonAll[T,mirror.MirroredElemTypes]

  }