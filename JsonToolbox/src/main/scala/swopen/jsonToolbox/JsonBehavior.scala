package swopen.jsonToolbox

import swopen.jsonToolbox.schema.JsonSchema
import swopen.jsonToolbox.codec.Encoder
import swopen.jsonToolbox.codec.{DecodeException,Decoder}
import swopen.jsonToolbox.json.Json
import scala.util.NotGiven
import shapeless3.deriving.*


object JsonBehavior:

  extension [T:Encoder](t:T)
    def encode(using encoder:Encoder[T]):Json = encoder.encode(t)

  extension (t:Json)
    def decode[T](using decoder:Decoder[T]):Either[DecodeException, T] = decoder.decode(t)