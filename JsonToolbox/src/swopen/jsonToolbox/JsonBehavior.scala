package swopen.jsonToolbox

import swopen.jsonToolbox.schema.JsonSchema
import swopen.jsonToolbox.codec.Encoder
import swopen.jsonToolbox.codec.Decoder
import swopen.jsonToolbox.json.Json


object JsonBehavior:
  extension [T](t:T)
    def encode(using encoder:Encoder[T]) = encoder.encode(t)

  extension (t:Json)
    def decode[T](using decoder:Decoder[T]) = decoder.decode(t)