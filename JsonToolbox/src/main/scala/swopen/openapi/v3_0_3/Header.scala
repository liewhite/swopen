package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json

case class HeaderInternal(
  description: Option[String],
  required: Option[Boolean],
  deprecated: Option[Boolean],
  allowEmptyValue: Option[Boolean],

  style: Option[String],
  explode: Option[Boolean],
  allowReserved: Option[Boolean],
  schema: Option[Schema],
  example: Option[Json],
  examples: Option[Map[String,OrRef[Example]]],

  content: Option[Map[String, MediaType]]
)
type Header = WithExtensions[HeaderInternal]