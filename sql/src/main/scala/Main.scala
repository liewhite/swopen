import io.github.liewhite.sql.*
import scala.compiletime.*

case class A(id: Long, short: Int, name:String)

@main def m():Unit = {
  val a = from[A]
  val b = from[A]
  val aid = a.id
  println(a.id.eql(123L))
  println(a.short.eql(123))
}