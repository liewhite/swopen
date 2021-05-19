package swopen.jsonToolbox.typeclasses
import scala.deriving.*
import swopen.jsonToolbox.utils.SummonUtils.summonAll


trait CoproductInst[T[_],A]:
  def elemT: List[T[Any]]
  def ordinal(a:A):Int

object CoproductInst:
  inline given [T[_],A](using mirror: Mirror.SumOf[A]): CoproductInst[T,A] = new CoproductInst[T,A]{
    def elemT = summonAll[T,mirror.MirroredElemTypes]

    def ordinal(a:A):Int = mirror.ordinal(a)

  }