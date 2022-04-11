package io.github.liewhite.config

import io.github.liewhite.json.codec.Decoder
import io.github.liewhite.json.JsonBehavior.*
import io.circe.yaml.parser
import scala.io.Source

def loadConfig[T: Decoder](path: String = "./conf/config.yaml"): Either[Exception, T] = {
    val file = Source.fromFile(path)
    try {
        parser.parse(file.mkString).flatMap(data => data.decode)
    } finally {
        file.close
    }
}
