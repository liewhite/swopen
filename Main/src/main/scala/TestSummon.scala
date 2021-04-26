import scala.compiletime.summonInline
import shapeless3.deriving.*
import swopen.jsonToolbox.codec.Encoder

trait Tk[T]:
  def hello:String

object Tk:
  given Tk[Int] with
    def hello = "int"

object S:
  given Tk[String] with
    def hello = "string1"
  
  inline def f[T] = 
    summon[Tk[String]].hello

  def f2[T](using t:Tk[T]) = t
    

// inline given Tk[String] with
//   def hello = "string2"

enum X:
  case A
  case B

@main def testSummon(): Unit = 
  // ???
  // import shapeless3.deriving.*
  println(summon[K0.Generic[X]])
  // println(S.f2[String].hello)