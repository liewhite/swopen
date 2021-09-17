package io.github.liewhite.json.contrib.mongodb

import io.github.liewhite.json.codec.Encoder
import io.github.liewhite.json

import org.bson.Document
import io.circe.Json
import io.github.liewhite.json.codec.Decoder
import io.github.liewhite.json.codec.DecodeException
import org.bson.types.ObjectId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MongoDateTime(val datetime: ZonedDateTime)

object MongoCodec {
  given Encoder[MongoDateTime] with {
    def encode(t: MongoDateTime) = Json.fromFields(
      Vector(
        "$date" -> Json.fromString(
          t.datetime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
      )
    )
  }
  
  given Decoder[MongoDateTime] with {
    def decode(
        data: Json,
        withDefaults: Boolean = true
    ): Either[DecodeException, MongoDateTime] = {
      (for {
        obj <- data.asObject
        value <- obj("$date")
        str <- value.asString
      } yield str).map(item => {
        MongoDateTime(
          ZonedDateTime.parse(item, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
      }) match {
        case Some(n) => Right(n)
        case None =>
          Decoder.decodeError("datetime ISO_OFFSET_DATE_TIME format", data)
      }
    }
  }

  given Encoder[ObjectId] with {
    def encode(t: ObjectId): Json = {
      Json.obj(("$oid" -> Json.fromString(t.toHexString)))
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
        case None =>
          Left(
            DecodeException("decode objectID failed with: \n" + data.spaces2)
          )
      }
    }
  }

  given Encoder[Document] with {
    def encode(t: Document): Json = {
      json.parseString(t.toJson).toOption.get
    }
  }

  given Decoder[Document] with {
    def decode(
        data: Json,
        withDefaults: Boolean = true
    ): Either[DecodeException, Document] = {
      try {
        Right(Document.parse(data.noSpaces))
      } catch {
        case e: Exception => Left(DecodeException(e.getMessage))
      }
    }
  }
}
