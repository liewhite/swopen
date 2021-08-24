import io.github.liewhite.sql.*
import scala.compiletime.*

case class CustomField(t: Int, t2: String)
object CustomField{
  given DBFieldLike[CustomField] with {
    def toField(fieldName:String, tableName:String): DBField = DBField(fieldName,tableName, DBFieldType.Text())
  }
}

case class A(id: Int,s: String)
case class B(idb: Int,sb: String, b: CustomField)

@main def m():Unit = {
  val c = 1
  val b = Table[A]
  val d = Table[B]
  val joined = Table[A].join(d).where(r => r.A.id.eql(r.B.idb)).select(r => r.A.id *: EmptyTuple)
  println(joined)
}
