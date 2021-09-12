package io.github.liewhite.json.contrib.datetime

import io.github.liewhite.json.codec.{Encoder,Decoder,DecodeException}
import org.bson.types.ObjectId
import io.circe.Json
import java.time.LocalDateTime
import java.time.ZonedDateTime


object MongoDatetimeCodec{

  given Encoder[LocalDateTime] with {
    def encode(t: LocalDateTime): Json = {
      Json.obj(("$dt" -> Json.fromString(t.toString)))
    }
  }

  given Decoder[ObjectId] with {
    def decode(
        data: Json,
        withDefaults: Boolean = true
    ): Either[DecodeException, ObjectId] = {
      (for {
        obj <- data.asObject
        value <- obj("$oid")
        hex <- value.asString
      } yield ObjectId(hex)) match {
        case Some(v) => Right(v)
        case None => Left(DecodeException("decode objectID failed with: \n" + data.spaces2))
      }
    }
  }
}