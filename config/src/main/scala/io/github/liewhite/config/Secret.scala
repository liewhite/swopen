package io.github.liewhite.config

import io.circe.Json
import zio.json.JsonEncoder
import zio.json.JsonDecoder

class Secret(val s: String) {
    override def toString: String = "******"
}

object Secret {
    given JsonEncoder[Secret] = JsonEncoder.string.contramap(_.s)
    given JsonDecoder[Secret] = JsonDecoder.string.map(Secret(_))
}
