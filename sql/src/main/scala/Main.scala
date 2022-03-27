package main
import io.github.liewhite.sql.*
import scala.compiletime.*
import io.github.liewhite.sql.Table
import io.github.liewhite.sql.annotation.{Unique, Index, ColumnName}
import scala.jdk.CollectionConverters.*

case class B(
    id: Int,
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

given conn: Connection =
  Connection("jdbc:mysql://localhost:3306/test", "sa", "123")
@main def main: Unit = {
  // val conn = Connection("jdbc:postgresql://localhost:5432/lee","lee","")
  conn.registerMigration[B]
  // conn.migration
  val t = Table[B]
  val sql = conn.jooqConn.select(t.Aa.toColumn).from(t).getSQL
  val col = t.Aa.toColumn
  val insert = conn.jooqConn
    .insertInto(t.table)
    .columns(t.Aa.toColumn, t.Bb.toColumn)
    .values(1,Some(1))

  println(insert.getParams)
}
