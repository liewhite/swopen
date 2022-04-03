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
import io.github.liewhite.sqlx.annotation.Primary
import io.getquill.context.jdbc.JdbcContext
import io.getquill.idiom.Idiom
import io.getquill.context.sql.idiom.SqlIdiom

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

case class G(
  a: Int,
  @Primary()
  id: Long = 0
){
  inline def create[A <: SqlIdiom, N <: NamingStrategy](ctx: JdbcContext[A,N]) = {
    import ctx._
    query[G].insertValue(this)
  }
}

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
  
  ctx.migrate[G]

  val g = G(1)

  inline def create = query[G].insert(p => (p.a -> 1))
  run(create)
  // Range(0, 1000).foreach(i => {
  // val rows = run(query[G].filter(item => true))
  // rows.foreach(item => {
  //   println(item)
  // })
  // })
}