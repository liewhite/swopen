package main
import scala.compiletime.*
import io.github.liewhite.sqlx.*
import io.github.liewhite.sqlx.annotation.{Unique, Index}
import scala.jdk.CollectionConverters.*
import javax.sql.DataSource
import io.getquill.*
import com.zaxxer.hikari.HikariDataSource
import java.time.ZonedDateTime
import io.github.liewhite.sqlx.annotation.Length

case class B(
    id: Int,
    // @Unique()
    idb: Int,
    //
    @Index("i_aa_bb")
    @Index("i_bb")
    Bb: Option[Int],
    //
    @Index("i_aa_bb")
    @Index("i_aa", unique = true)
    Aa: Int = 1123, // index 要求顺序， 但是默认值
    d: Int = 1123 // index 要求顺序， 但是默认值
)

case class F(
  Dt : Option[ZonedDateTime],
  Value: Option[BigInt],
  Address: Array[Byte],
)

@main def main: Unit = {
  val a = 1
  // val ctx = getDBContext[MySQLDialect.type](DBConfig(
  //     host = "localhost",
  //     username = "sa",
  //     password = Some("123"),
  //     db = "test"
  //   )
  // )
  val ctx = getDBContext[PostgresDialect.type](
    DBConfig(
      host = "localhost",
      username = "lee",
      db = "test"
    )
  )
  import ctx._
  ctx.migrate[F]

  inline def newc = query[F].insertValue(lift(F(Some(ZonedDateTime.now), Some(BigInt(-123)), "1111".getBytes)))
  // Range(0, 1000).foreach(i => {
  run(newc)
  val rows = run(query[F].filter(item => true))
  rows.foreach(item => {
    println(item)
  })
  // })
}