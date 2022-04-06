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
  s: String,
  i: BigInt,
  dt: ZonedDateTime,
  opt: Option[ZonedDateTime],
)


// extension (inline left: ZonedDateTime) {
//   inline def >(right: ZonedDateTime) =  quote(infix"$left > $right".pure.as[Boolean])
//   inline def <(right: ZonedDateTime) =  quote(infix"$left < $right".pure.as[Boolean])
// }

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
  // Range(0,1000).foreach(i => {
  //   val data = Range(0,100).map(item => {
  //     F("1",1,ZonedDateTime.now, Some(ZonedDateTime.now))
  //   })
  //   run(liftQuery(data).foreach(i => query[F].insertValue(i)))
  // })
  val a = BigDecimal(1)
  val b = BigDecimal(1)
  inline def c = a > b

  val result = run(query[F].filter(item2 => item2.i gt lift(1)))

  // inline def select2 =quote{ query[F].filter(item => item.i > lift(BigInt(1))) }
  // ctx.run(select2)

  inline def select3 =quote{ query[F].filter(item => liftQuery(Vector(1)).contains(item.i)) }
  // inline def select3 =quote{ query[F].filter(item => item in Vector(1,2,3)) }
  val result2 = run(select3)
  // ctx.run(select3)
  // rows.foreach(item => {
  //   println(item)
  // })
}