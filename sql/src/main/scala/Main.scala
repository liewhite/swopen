import io.github.liewhite.sql.*
import scala.compiletime.*
import java.beans.Customizer

case class CustomField(t: Int, t2: String)

object CustomField{
  given DBValueConverter[CustomField] with {
    def dbRepr: DBType = DBType.VarChar(255)
    def convert(t:CustomField):DBValue[CustomField] = {
      new DBValue[CustomField]{
        def toExpr:Expr = Expr(s"${t.t}+ ${t.t2}")
      }
    }
  }
}

case class A(id: Long, short: Int, name:String,d: CustomField)

@main def m():Unit = {
  val a = from[A]
  // val b = from[A]
  val aid = a.id
  println(a.id.eql(123L).or(a.name.eql("asd")).not.render)
  println(a.short.eql(123).render)
}