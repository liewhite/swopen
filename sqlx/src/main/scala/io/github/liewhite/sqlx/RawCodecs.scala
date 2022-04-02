package io.github.liewhite.sqlx

import io.getquill.*
import io.getquill.context.jdbc.JdbcContext
import java.time.ZonedDateTime
import io.getquill.idiom.Idiom
import cats.implicits
import io.getquill.context.sql.idiom.SqlIdiom
// import io.getquill.MappedEncoding

trait RawCodecs[I<:SqlIdiom, N <: NamingStrategy] { this: JdbcContext[I, N] =>
  implicit val bigIntDecoder: Decoder[BigInt] = 
    decoder(row => index => {
        BigInt(row.getObject(index).toString)
    }) 
  implicit val bigIntEncoder:  Encoder[BigInt] =  {
    encoder(
      java.sql.Types.NUMERIC,
      (index, value, row) => row.setObject(index, java.math.BigDecimal(value.toString), java.sql.Types.NUMERIC)
    )
  }

  // given Encoder[ZonedDateTime] =
  //   encoder(
  //     java.sql.Types.NUMERIC,
  //     (index, value, row) => row.setObject(index, value, java.sql.Types.NUMERIC)
  //   )
  // given Decoder[ZonedDateTime] =
  //   decoder(row => index => {???
  //   }) 

}
