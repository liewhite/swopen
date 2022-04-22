package io.github.liewhite.config

import io.github.liewhite.json.SwopenJson.*
import io.circe.yaml.parser
import scala.io.Source

def loadConfig[T: ReadWriter](path: String = "./conf/config.yaml"): T = {
    val file = Source.fromFile(path)
    try {
        val cfg = parser.parse(file.mkString).map(data => read[T](data.noSpaces)) match {
            case Left(e) => throw e
            case Right(o) => o
        }
        cfg
    } finally {
        file.close
    }
}
