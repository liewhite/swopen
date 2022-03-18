import io.github.liewhite.sql.*
import scala.compiletime.*
import io.github.liewhite.sql.Table
import io.github.liewhite.sql.annotation.{Unique, Index, ColumnName}

case class B(
    @Unique()
    idb: Int,
    //
    @Index("i_aa_bb")
    @Index("i_bb")
    @ColumnName("bb")
    Bb: Option[Int],
    //
    @ColumnName("aa")
    @Index("i_aa_bb") 
    @Index("i_aa", unique = true)
    Aa: Int = 1123 // index 要求顺序， 但是默认值
) derives Table

case class XX(a: Int = 1, b: String)

@main def m(): Unit = {
  val d = Table[B]
  println(s"select ${d.Aa.queryName} from ${d.tableName}")
  val xx = XX(a = 1, b = "xxx")
  println(xx)
}
