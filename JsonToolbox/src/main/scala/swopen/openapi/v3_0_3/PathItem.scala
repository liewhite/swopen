package swopen.openapi.v3_0_3

import swopen.jsonToolbox.codec.*

@IgnoreNull()
case class PathItemInternal(
  `$ref`: Option[String],
  summary: Option[String],
  description: Option[String],

  get: Option[PathOperation],
  post: Option[PathOperation],
  delete: Option[PathOperation],
  put: Option[PathOperation],
  head: Option[PathOperation],
  patch: Option[PathOperation],
  options: Option[PathOperation],
  trace: Option[PathOperation],

  servers: Option[Vector[Server]],
  parameters: Option[Vector[Parameter|RefTo]]
) derives Encoder,Decoder

type PathItem = WithExtensions[PathItemInternal]