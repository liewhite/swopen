package swopen.openapi

import swopen.jsonToolbox.json.Json

case class HeaderInternal(
  description: Option[String],
  required: Option[Boolean],
  deprecated: Option[Boolean],
  allowEmptyValue: Option[Boolean],

  style: Option[String],
  explode: Option[Boolean],
  allowReserved: Option[Boolean],
  schema: Option[OrRef[Schema]],
  example: Option[Json],
  examples: Option[Map[String,OrRef[Example]]],

  content: Option[Map[String, MediaType]]
)
type Header = WithExtensions[HeaderInternal]