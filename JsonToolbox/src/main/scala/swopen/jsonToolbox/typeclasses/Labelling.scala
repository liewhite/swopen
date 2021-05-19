package swopen.jsonToolbox.typeclasses

import scala.deriving.*
import scala.compiletime.*
import scala.collection.immutable.ArraySeq
import swopen.jsonToolbox.utils.SummonUtils.*

case class Labelling[T](label: String, elemLabels: Seq[String])
object Labelling {
  inline given apply[T0](using mirror: Mirror { type MirroredType = T0 }): Labelling[T0] =
    Labelling[T0](
      constValue[mirror.MirroredLabel & String],
      ArraySeq.unsafeWrapArray(summonValuesAsArray[mirror.MirroredElemLabels, String])
    )
}