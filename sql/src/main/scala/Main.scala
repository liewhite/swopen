import io.github.liewhite.sql.*
import scala.compiletime.*
import java.beans.Customizer

case class CustomField(t: Int, t2: String)

case class A(id: Int,s:String)

@main def m():Unit = {
  val b = Table[A]
  val c = Table[CustomField]
  val joined = b.join(c)
  println(joined.A.id)
  // val id = a.id
}