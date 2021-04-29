import shapeless3.deriving.Annotation
import scala.compiletime.summonFrom
import scala.util.NotGiven
import swopen.jsonToolbox.schema.DefaultValue

class Modifier() extends scala.annotation.Annotation

trait OptionGiven[T]:
  def give: Option[T]

object OptionGiven:
  given exist[T](using NotGiven[T]): OptionGiven[T] with
    def give = None

  given notExist[T](using t: T): OptionGiven[T] with
    def give = Some(t)


case class A(nodefalt:Int,a:Int = 1,b:Vector[Map[String,Int]] = Vector(Map("asd" -> 123)))


def f[T](using a: OptionGiven[T]) = a


inline def mySummon[T] = summonFrom {
  case t: T => t
  case _ => print("-----------------------:"); println(_)
}

given Int = 3

def default[A](using a:DefaultValue[A]) = a.defaults
@main def test(): Unit = 
  val ok = mySummon[Double]
  println(ok)
  println(default[A])
  // val compileError = println(f[Int].give) //No Annotation of type Modifier for type A
  myMacro
