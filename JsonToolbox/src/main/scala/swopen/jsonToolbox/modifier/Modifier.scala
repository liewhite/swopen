package swopen.jsonToolbox.modifier

import shapeless3.deriving.Annotation
import scala.util.NotGiven

trait ModifierGiven[A,T]:
  def give: Option[Annotation[A,T]]

object ModifierGiven:
  given [A,T](using NotGiven[Annotation[A,T]]):Option[Annotation[A,T]] = None
  given [A,T](using a:Annotation[A,T]):Option[Annotation[A,T]] = Some(a)

  given [A,T](using a: Option[Annotation[A,T]]): ModifierGiven[A,T] = 
    new ModifierGiven:
      def give = a

class Modifier(val rename: String) extends scala.annotation.Annotation