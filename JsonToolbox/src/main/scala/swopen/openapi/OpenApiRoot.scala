package swopen.openapi

import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.schema.JsonSchema
import swopen.jsonToolbox.codec.{Encoder,Decoder, DecodeException}



case class OpenApiRoot(
  openapi: String,
  info: Info, 
  servers: Option[Vector[Server]],
  paths: Vector[WithExtensions[Map[String,PathItem]]],
  // components: Option[OpenApiComponents],
  // security: Option[Vector[OpenApiSecurity]],
  // tags: Option[Vector[OpenApiTag]],
  // externalDocs: Option[Vector[OpenApiExternalDoc]],
)