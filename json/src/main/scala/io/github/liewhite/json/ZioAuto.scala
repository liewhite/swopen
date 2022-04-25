package io.github.liewhite.json

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import scala.deriving.Mirror
import zio.json.JsonCodec
import zio.json.DeriveJsonCodec
import zio.json.JsonDecoder

inline given [D: Mirror.Of]: JsonCodec[D] =
    DeriveJsonCodec.gen[D]
export zio.json.{JsonEncoder, JsonDecoder, JsonCodec}

extension (s: String) {
    def fromJsonMust[A](implicit decoder: JsonDecoder[A]): A = {
        decoder.decodeJson(s) match {
            case Left(e)  => throw Exception(e)
            case Right(o) => o
        }
    }
}

extension [A: JsonEncoder](s: A) {
    def toJson(implicit encoder: JsonEncoder[A]): String = {
        encoder.encodeJson(s, None).toString
    }
}
