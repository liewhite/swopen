package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json

case class ParameterInternal(
  name: String,
  in: String,
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
type Parameter = WithExtensions[ParameterInternal]