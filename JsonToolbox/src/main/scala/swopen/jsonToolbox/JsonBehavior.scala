package swopen.jsonToolbox

import swopen.jsonToolbox.schema.{JsonSchema,QualifiedName}
import swopen.jsonToolbox.codec.Encoder
import swopen.jsonToolbox.codec.{DecodeException,Decoder}
import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.modifier.Modifier
import swopen.openapi.v3_0_3.Schema
import scala.util.NotGiven
import scala.compiletime.summonInline
// import shapeless3.deriving.*


/**
 * encode -> schema(modify -> validate) -> serialze
 * deserialze -> schema(validate -> modify) -> decode
 * //暂时可以不实现modify, 就rennme一个需求。 可以直接用反引号写
 * 
 */
object JsonBehavior:
  extension [T](t:T)
    def encode(using encoder:  Encoder[T]):Json =
      encoder.encode(t)

  extension (t:Json)
    def decode[T](using decoder:Decoder[T]):Either[DecodeException, T] = decoder.decode(t)
  
  inline def schema[T](using o: JsonSchema[T]):Schema =
    o.schema
