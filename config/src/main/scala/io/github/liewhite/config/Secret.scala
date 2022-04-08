package io.github.liewhite.config

import io.github.liewhite.json.codec.{Encoder, Decoder,DecodeException}
import io.circe.Json

class Secret(val s: String) {
    override def toString: String = "******"
}

object Secret {
    given Encoder[Secret] with {
        def encode(t: Secret): Json = Json.fromString(t.toString)
    }

    given Decoder[Secret] with {
        def decode(
            data: Json,
            withDefaults: Boolean = true
        ): Either[DecodeException, Secret] = {
            data.asString.map(Secret(_)) match {
                case Some(o) => Right(o)
                case None => Left(DecodeException("failed deocde secret"))
            }
        }
    }
}
