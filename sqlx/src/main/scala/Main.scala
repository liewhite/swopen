package main
import scala.compiletime.*
import io.github.liewhite.sqlx.*
import io.github.liewhite.sqlx.annotation.{Unique, Index, ColumnName}
import scala.jdk.CollectionConverters.*
import javax.sql.DataSource
import io.getquill.*
import com.zaxxer.hikari.HikariDataSource
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Date
import java.time.ZoneId
import java.math.BigInteger
import io.github.liewhite.sqlx.annotation.Length

case class B(
    id: Int,
    // @Unique()
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
    Aa: Int = 1123, // index 要求顺序， 但是默认值
    d: Int = 1123 // index 要求顺序， 但是默认值
)

case class C(
    // @Unique()
    // @Index("aa", unique=true)
    a: Int = 1,
    // @Unique()
    @Index("bde")
    // @Index("bdeu", unique=true)
    @Length(33)
    b: String,
    // @Index("bde")
    @Index("bdeu", unique=true)
    d: BigInt = BigInt(123),
    @Index("bde")
    @Index("bdeu", unique=true)
    e: Option[Boolean],
    // @Index("f_unique", unique=true)
    // @Unique()
    // @Index("f_normal")
    f: Option[String]
)

@main def main: Unit = {
  val a = 1
  val ctx = getDBContext[MySQLDialect.type](DBConfig(
      host = "localhost",
      username = "sa",
      password = Some("123"),
      db = "test"
    )
  )
  // val ctx = getDBContext[PostgresDialect.type](
  //   DBConfig(
  //     host = "localhost",
  //     username = "lee",
  //     db = "test"
  //   )
  // )
  import ctx._
  ctx.migrate[C]

  // inline def newc(i: Int) =
  //   query[C].insertValue(lift(C(i, "asd", i, true, Some(i))))
  // Range(0, 1000).foreach(i => {
  //   run(newc(i))
  // })
}