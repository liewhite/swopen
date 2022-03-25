package main
import io.github.liewhite.sql.*
import scala.compiletime.*
import io.github.liewhite.sql.Table
import io.github.liewhite.sql.annotation.{Unique, Index, ColumnName}

case class B(
    id :Int,

    @Unique()
    idb: Int,
    //
    @Index("i_aa_bb")
    @Index("i_bb")
    @ColumnName("bb")
    Bb: Option[Int],
    //
    @Index("i_aa_bb") 
    @Index("i_aa", unique = true)
    @ColumnName("aa")
    Aa: Int = 1123 // index 要求顺序， 但是默认值
)

@main def main:Unit = {
  // val conn = Connection("jdbc:postgresql://localhost:5432/lee","lee","")
  val conn = Connection("jdbc:mysql://localhost:3306/test","sa","123")
  conn.registerMigration[B]
  conn.migration
  println("xxx")
}
// @main def x: Unit = {
// }