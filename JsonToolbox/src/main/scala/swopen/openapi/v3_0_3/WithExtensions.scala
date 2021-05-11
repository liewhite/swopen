package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.schema.JsonSchema
import swopen.jsonToolbox.codec.{Encoder,Decoder, DecodeException}

/**
 * 
 * @param spec OpenApi标准内的字段
 * @param addtionalInfo 扩展字段， 多为 `x-` 开头
 */
case class WithExtensions[T](spec:T,additionalInfo: Option[Map[String,Json]] = None)

object WithExtensions:
  given [T:Encoder](using encoder: Encoder[T]): Encoder[WithExtensions[T]] with
    def encode(t: WithExtensions[T]) = 
      val spec = encoder.encode(t.spec)
      spec match
        case Json.JObject(obj) =>
          val extraKv = t.additionalInfo match
            case Some(info) => info.encode().asInstanceOf[Json.JObject].value
            case None => Map.empty
          val newMap = obj ++ extraKv
          Json.JObject(newMap)
        case other => other

  given [T:Decoder]: Decoder[WithExtensions[T]] with
    def decode(data:Json): Either[DecodeException, WithExtensions[T]] = 
      data match
        case Json.JObject(obj) =>
          val spec = Json.JObject(obj.filter(item => !item._1.startsWith("x-"))).decode[T] 
          val extra = Json.JObject(obj.filter(item => item._1.startsWith("x-"))).decode[Map[String,Json]]
          spec match 
            case Right(s) => extra match
              case Right(e) => 
                if e.isEmpty then
                  Right(new WithExtensions(s, None))
                else
                  Right(new WithExtensions(s, Some(e)))
              case Left(err) => Left(err)
            case Left(serr) => Left(serr)
        case other => Left(new DecodeException(s"must be object, got: ${data.serialize}"))