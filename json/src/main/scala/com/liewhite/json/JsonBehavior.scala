package com.liewhite.json

import scala.util.NotGiven
import scala.compiletime.summonInline

import com.liewhite.json.codec.Encoder
import com.liewhite.json.codec.{DecodeException,Decoder}
import io.circe.Json


/**
 * encode -> schema(modify -> validate) -> serialze
 * deserialze -> schema(validate -> modify) -> decode
 * //暂时可以不实现modify, 就rennme一个需求。 可以直接用反引号写
 * 
 */
object JsonBehavior:
  extension [T](t:T)
    def encode(using encoder:  Encoder[T]): Json =
      encoder.encode(t)

  extension (t:Json)
    def decode[T](using decoder:Decoder[T]):Either[DecodeException, T] = decoder.decode(t)
  