package io.github.liewhite.config

import sys.env
import scala.io.Source
import scala.deriving.*
import scala.jdk.CollectionConverters.*
import scala.quoted.*
import scala.util.NotGiven
import scala.compiletime.*
import java.math.BigInteger
import scala.reflect.ClassTag
import shapeless3.deriving.*
import io.github.liewhite.json.typeclass.*
import io.github.liewhite.json.annotations.*

import io.circe.Json
import io.circe.yaml.parser
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import io.github.liewhite.json.codec.DecodeException
import java.io.InputStreamReader
import io.github.liewhite.json.codec.Decoder

trait Configable[T] {
  def fromConfig(prefix: String, configs: Vector[Map[String, Json]]): T
}

object Configable {

  inline def derived[T](using
      gen: K0.ProductGeneric[T],
      labelling: Labelling[T]
  ): Configable[T] =
    product

  def getKey(prefix: String, configs: Vector[Map[String, Json]]): Json = {
    if (configs.isEmpty) {
      throw ConfigError(prefix)
    } else {
      configs.head.get(prefix) match {
        case Some(v) => v
        case None    => getKey(prefix, configs.tail)
      }
    }
  }
  given Configable[Int] with {
    def fromConfig(
        prefix: String,
        configs: Vector[Map[String, Json]]
    ): Int = {
      getKey(prefix, configs).asNumber match {
        case Some(v) => v.toInt.get
        case None => {
          getKey(prefix, configs).asString.get.toInt
        }
      }
    }
  }
  given Configable[String] with {
    def fromConfig(
        prefix: String,
        configs: Vector[Map[String, Json]]
    ): String = {
      getKey(prefix, configs).asString.get
    }
  }

  given Configable[Boolean] with {
    def fromConfig(
        prefix: String,
        configs: Vector[Map[String, Json]]
    ): Boolean = {
      getKey(prefix, configs).asBoolean match {
        case Some(v) => v
        case None => {
          getKey(prefix, configs).asString.get.toBoolean
        }
      }
    }
  }

  given product[T](using
      inst: => K0.ProductInstances[Configable, T],
      labelling: Labelling[T],
      defaults: DefaultValue[T]
  ): Configable[T] with {
    def fromConfig(prefix: String, configs: Vector[Map[String, Json]]): T = {
      val dft = defaults.defaults
      val labels = labelling.elemLabels
      var i = 0
      inst.construct(
        [t] =>
          (decoder: Configable[t]) => {
            val result = decoder.fromConfig(
              if (prefix.isEmpty) {labels(i)} else {Vector(prefix, labels(i)).mkString("_")},
              configs.appended(dft) 
            )
            i += 1
            result
        }
      )
    }
  }

  def configure[T](
      configs: Vector[Map[String, Json]]
  )(using decoder: Configable[T]): T = {
    decoder.fromConfig("", configs)
  }

  def defaultConfigsWithEnv(paths: Vector[String] = Vector("config/local.yml","config/default.yml")): Vector[Map[String,Json]] = {
    paths.map(path => {
      try{
        parser.parse(Source.fromFile(path).reader) match {
          case Left(e) => Map.empty[String,Json]
          case Right(j) => {
            j.asObject.get.toMap
          }
        }
      }
      catch {
        case e => {
          Map.empty[String,Json]
        }
      }
    }).foldLeft(Vector.empty[Map[String,Json]])((acc,item) => {
      acc.appended(item)
    }).prepended(sys.env.map{(k,v ) => (k, Json.fromString(v))})

  }
}
