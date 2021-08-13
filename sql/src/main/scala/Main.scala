import io.github.liewhite.sql.Table
import com.typesafe.config.ConfigFactory
// import io.getquill._
import io.github.liewhite.sql.props
import scala.jdk.CollectionConverters._

case class A(id: Int, name:String)

@main def m():Unit = {
  val a = props[A]
  val id = a.id
  // 返回column[String], 这样就能通过实现 < 而生成condition，而且还不会破坏类型安全,
  // trait IntField[T]]{ def <[THAT:IntField](that: THAT) }, 数字实现了IntField, a.id会直接返回一个IntField
  //
  println(a.id <  "xx")
  println(a.name)
}