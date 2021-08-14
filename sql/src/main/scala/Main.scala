import io.github.liewhite.sql.Table
import com.typesafe.config.ConfigFactory
import io.github.liewhite.sql.*
import scala.jdk.CollectionConverters._
import scala.language.implicitConversions
import scala.compiletime.*
import io.github.liewhite.sql.DBValue.given

case class A(id: Int, name:String)

@main def m():Unit = {
  val a = from[A]
  val id = a.name
  val v = new DBValue{
    type Underlying = "integer"
    def toExpr = "asd"
  }
  // val v2: DBValue = 123
  // val x = constValue[v2.Underlying]
  // println(x)
  println("name".eql(a.name))


  // 返回column[String], 这样就能通过实现 < 而生成condition，而且还不会破坏类型安全,
  // trait IntField[T]]{ def <[THAT:IntField](that: THAT) }, 数字实现了IntField, a.id会直接返回一个IntField
}