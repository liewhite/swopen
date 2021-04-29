package swopen.openapi

import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.schema.JsonSchema
import swopen.jsonToolbox.codec.{Encoder,Decoder, DecodeException}

/**
 * 
 * @param spec OpenApi标准内的字段
 * @param addtionalInfo 扩展字段， 多为 `x-` 开头
 */
class OpenApiObject[T](val spec:T,val additionalInfo: Option[Map[String,Json]])

object OpenApiObject:
  given [T:Encoder:JsonSchema](using encoder: Encoder[T]): Encoder[OpenApiObject[T]] with
    def encode(t: OpenApiObject[T]) = 
      val spec = encoder.encode(t.spec)
      spec match
        case Json.JObject(obj) =>
          val extraKv = t.additionalInfo match
            case Some(info) => info.encode().asInstanceOf[Json.JObject].value
            case None => Map.empty
          val newMap = obj ++ extraKv
          Json.JObject(newMap)
        case other => other

  given [T:Decoder:JsonSchema]: Decoder[OpenApiObject[T]] with
    def decode(data:Json): Either[DecodeException, OpenApiObject[T]] = 
      data match
        case Json.JObject(obj) =>
          val spec = Json.JObject(obj.filter(item => !item._1.startsWith("x-"))).decode[T] 
          val extra = Json.JObject(obj.filter(item => item._1.startsWith("x-"))).decode[Map[String,Json]]
          spec match 
            case Right(s) => extra match
              case Right(e) => 
                if e.isEmpty then
                  Right(new OpenApiObject(s, None))
                else
                  Right(new OpenApiObject(s, Some(e)))
              case Left(err) => Left(err)
            case Left(serr) => Left(serr)
        case other => Left(new DecodeException(s"must be object, got: ${data.serialize}"))