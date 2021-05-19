package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.{Encoder,Decoder, DecodeException}

enum OrRef[+T]:
  case Id(t:T)
  case Ref(`$ref`: String)
end OrRef

object OrRef:
  given [T](using encoder: Encoder[T]): Encoder[OrRef[T]] with
    def encode(t:OrRef[T]):Json = 
      t match
        case Id(t) => encoder.encode(t)
        case Ref(ref) => Map("$ref" -> ref).encode

  given [T](using decoder:  Decoder[T]): Decoder[OrRef[T]] with
    def decode(t:Json):Either[DecodeException, OrRef[T]] = 
      t match
        case Json.JObject(map) => 
          if map.contains("$ref") then
            val ref = map("$ref")
            ref match
              case Json.JString(str) => Right(Ref(str))
              case other => Left(DecodeException(s"expect string, got: ${other.serialize}"))
          else
            decoder.decode(t) match
              case Right(v) => Right(Id(v))
              case Left(e) => Left(e)
        case other => Left(DecodeException(s"expect object, got: ${other.serialize}"))
