package io.github.liewhite.sqlx

import io.getquill.*
import io.getquill.context.jdbc.JdbcContext
import java.time.ZonedDateTime
import io.getquill.idiom.Idiom
import io.getquill.context.sql.idiom.SqlIdiom
import java.util.Date
import java.time.Instant
import java.time.ZoneId
import io.getquill.parser.engine.Parser
import scala.quoted.Quotes

trait RawCodecs[I <: SqlIdiom, N <: NamingStrategy] { this: JdbcContext[I, N] =>
    implicit val bigIntDecoder: Decoder[BigInt] =
        decoder(row =>
            index => {
                BigInt(row.getObject(index).toString)
            }
        )

    implicit val bigIntEncoder: Encoder[BigInt] = {
        encoder(
          java.sql.Types.NUMERIC,
          (index, value, row) =>
              row.setObject(
                index,
                java.math.BigDecimal(value.toString),
                java.sql.Types.NUMERIC
              )
        )
    }

    implicit val zdD: Decoder[ZonedDateTime] =
        decoder(row =>
            index => {
                ZonedDateTime.ofInstant(
                  Instant.ofEpochMilli(row.getLong(index)),
                  ZoneId.systemDefault
                )
            }
        )

    implicit val dtEncoder: Encoder[ZonedDateTime] = {
        encoder(
          java.sql.Types.BIGINT,
          (index, value, row) =>
              row.setObject(
                index,
                value.toInstant.toEpochMilli,
                java.sql.Types.BIGINT
              )
        )
    }

    // extension (inline left: ZonedDateTime) {
    //   inline def > (right: ZonedDateTime) = quote(
    //     infix"$left > $right".pure.as[Boolean]
    //   )
    //   inline def >=(right: ZonedDateTime) = quote(
    //     infix"$left >= $right".pure.as[Boolean]
    //   )
    //   inline def < (right: ZonedDateTime) = quote(
    //     infix"$left < $right".pure.as[Boolean]
    //   )
    //   inline def <=(right: ZonedDateTime) = quote(
    //     infix"$left <= $right".pure.as[Boolean]
    //   )
    //   inline def ==(right: ZonedDateTime) = quote(
    //     infix"$left = $right".pure.as[Boolean]
    //   )
    // }

    // extension (inline left: scala.math.BigInt) {
    //   inline def > (right: BigInt) = quote(
    //     infix"$left > $right".pure.as[Boolean]
    //   )
    // }

    extension [T](inline left: T) {
        inline def gt(right: T)  = quote(infix"$left > $right".pure.as[Boolean])
        inline def gte(right: T) = quote(infix"$left >= $right".pure.as[Boolean])
        inline def lt(right: T)  = quote(infix"$left < $right".pure.as[Boolean])
        inline def lte(right: T) = quote(infix"$left <= $right".pure.as[Boolean])
        inline def eq(right: T)  = quote(infix"$left = $right".pure.as[Boolean])
        // 没有seq 的 encoder
        // inline def in (right: Seq[T]) = quote(infix"$left in $right".pure.as[Boolean])
    }

    // mysql for update clause
    extension [T](inline q: Query[T]) {
        inline def forUpdate = quote(infix"$q FOR UPDATE".as[Query[T]])
    }
}
