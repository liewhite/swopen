package io.github.liewhite.sqlx

import io.getquill.*
import io.getquill.context.jdbc.JdbcContext
import java.time.ZonedDateTime
import io.getquill.idiom.Idiom
import cats.implicits
import io.getquill.context.sql.idiom.SqlIdiom
import java.util.Date
import java.time.Instant
import java.time.ZoneId

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
  implicit val zdD: Decoder[ZonedDateTime] = 
    decoder(row => index => {
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(row.getLong(index)), ZoneId.systemDefault)
    }) 

  implicit val zdE:  Encoder[ZonedDateTime] =  {
    encoder(
      java.sql.Types.BIGINT,
      (index, value, row) => row.setObject(index, value.toInstant.toEpochMilli, java.sql.Types.BIGINT)
    )
  }

  // implicit val bsD: Decoder[Array[Byte]] = 
  //   decoder(row => index => {
  //     row.getBytes(index)
  //   }) 

  // implicit val bsE:  Encoder[Array[Byte]] =  {
  //   encoder(
  //     java.sql.Types.BLOB,
  //     (index, value, row) => row.setObject(index, row.setBytes(index,value), java.sql.Types.BLOB)
  //   )
  // }
}
