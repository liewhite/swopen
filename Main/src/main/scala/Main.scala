import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.json.{Json,JsonNumber}
import swopen.jsonToolbox.modifier.Modifier
import swopen.jsonToolbox.codec.Encoder
import swopen.jsonToolbox.schema.JsonSchema
import scala.io.Source
import scala.deriving.*
import scala.math.BigInt
import shapeless3.deriving.*

trait T[T]:
  def hello:String

object T:
  given T[Opt] with
    def hello = "T"

  given T[G] with
    def hello = "T"


// @Modifier(rename="xx")
case class A()

sealed trait G
case class AG(a:Int) extends G
@Modifier(rename="b111g")
case object BG extends G

case class XX()

object XX
  given XXX[XX] with
    def hello = "xx"

trait XXX[T]:
  def hello:String

// object XXX:
//   given XXX[XX] with
//     def hello = "xxx"



def f(using x:XXX[XX]) = x.hello

enum Opt:
  case A(a:String)
  @Modifier(rename="b111g")
  // case object, 
  case B()
  case C()
end Opt
@main def test(): Unit = 
  // println(summon[Mirror.Of[Opt.C]].getClass)
  // println(summon[K0.Generic[Opt.B.type]].getClass)
  // println(summon[K0.CoproductGeneric[Opt]])
  println(summon[JsonSchema[Opt]].schema)
  // println(summon[Int])
  // println(f)
  val opt:Opt = Opt.B()
  println(opt.encode)
end test
  