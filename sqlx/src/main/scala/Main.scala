package main
import scala.compiletime.*
import io.github.liewhite.sqlx.*
import io.github.liewhite.common.Snowflake
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
  @Unique
  fId: Long,
  a: BigInt,
  @Length(35)
  b: String,
  c: ZonedDateTime,
  @Length(35)
  d: String,
)

@main def main: Unit = {
  val idg = Snowflake(123)
  // val a = 1
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
  ctx.migrate[F]
  // Range(0,10000).foreach(i => {
  //   val data = Range(0,100).map(item => {
  //     F(idg.nextId,BigInt(10), "str", ZonedDateTime.now)
  //   })
  //   run(liftQuery(data).foreach(i => query[F].insertValue(i)))
  // })

  // val rows = run(query[F].filter(item => true))
  // rows.foreach(item => {
  //   println(item)
  // })
  // })
}