package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.{Encoder,Decoder, DecodeException}



import swopen.jsonToolbox.codec.IgnoreNull


@IgnoreNull()
case class OpenApiRoot(
  openapi: String,
  info: Info, 
  servers: Option[Vector[Server]],
  paths: Vector[WithExtensions[Map[String,PathItem]]],
  components: Option[Components],
  // todo support keys below
  // security: Option[Vector[OpenApiSecurity]],
  // tags: Option[Vector[OpenApiTag]],
  // externalDocs: Option[Vector[OpenApiExternalDoc]],
)