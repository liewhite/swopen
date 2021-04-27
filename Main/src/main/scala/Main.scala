import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.modifier.Modifier
import swopen.jsonToolbox.utils.OptionGiven
// import swopen.jsonToolbox.codec.Encoder
// import scala.deriving.*
// import scala.compiletime.*
import shapeless3.deriving.*


enum Opt:
  val x = 3
  val y = 4
  case A(a:String)
  case B
  case C


case class A(a:Int)


def f[T](using a: OptionGiven[Annotation[Modifier,T]]) = a

@main def test(): Unit = 
  // summon[OptionGiven[Annotation[Modifier,A]]]
  // println(f[A]) //此处报错
  println(Opt.B.encode)
  println(Opt.A("a").encode)
  