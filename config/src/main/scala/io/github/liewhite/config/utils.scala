package io.github.liewhite.config

import io.github.liewhite.json.codec.Decoder
import io.github.liewhite.json.JsonBehavior.*
import io.circe.yaml.parser
import scala.io.Source

def loadConfig[T: Decoder](path: String = "./conf/config.yaml"): Either[Exception, T] = {
    parser.parse(Source.fromFile(path).mkString).flatMap(data=> data.decode)
}