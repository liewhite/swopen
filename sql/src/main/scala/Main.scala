import io.github.liewhite.sql.*
import scala.compiletime.*
import java.beans.Customizer

case class CustomField(t: Int, t2: String)

case class A(id: Int,s:String)
case class B(idb: Int,sb:String)

@main def m():Unit = {
  val b = Table[A]
  val c = Table[CustomField]
  val d = Table[B]
  val joined = b.join(c).join(d).where(r => r.A.id.eql(r.B.idb))
  println(joined)
}