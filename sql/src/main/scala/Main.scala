import io.github.liewhite.sql.*
import scala.compiletime.*
import io.github.liewhite.sql.Table

case class B(idb: Int)

@main def m():Unit = {
  val d = Table[B]
  println(d.idb.name)
}
