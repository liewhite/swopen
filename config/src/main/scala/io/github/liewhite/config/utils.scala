package io.github.liewhite.config

import io.circe.yaml.parser
import scala.io.Source
import zio.json._

def loadConfig[T: JsonDecoder](path: String = "./conf/config.yaml"): T = {
    val file = Source.fromFile(path)
    try {
        val cfg = parser.parse(file.mkString) match {
            case Left(e) => throw e
            case Right(o) => o
        }
        cfg.noSpaces.fromJson[T] match {
            case Left(e) => throw Exception(e)
            case Right(o) => o
        }
    } finally {
        file.close
    }
}
