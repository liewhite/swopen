package swopen.jsonToolbox

import swopen.jsonToolbox.schema.{JsonSchema,Schema}
import swopen.jsonToolbox.codec.Encoder
import swopen.jsonToolbox.codec.{DecodeException,Decoder}
import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.modifier.Modifier
import scala.util.NotGiven
import shapeless3.deriving.*


/**
 * 
 * encode -> (modify) -> validate -> serialize
 * deserialize -> validate -> (modify) -> decode 
 * //暂时可以不实现modify, 就rennme一个需求。 可以直接用反引号写
 * 
 */
object JsonBehavior:

  extension [T](t:T)
    def encode(modify: Boolean = true, validate:Boolean = true)(using encoder:Encoder[T] ):Json =
      encoder.encode(t)

  extension (t:Json)
    def decode[T](using decoder:Decoder[T]):Either[DecodeException, T] = decoder.decode(t)
  
  def schema[T:JsonSchema](using o: JsonSchema[T]):Schema = o.schema