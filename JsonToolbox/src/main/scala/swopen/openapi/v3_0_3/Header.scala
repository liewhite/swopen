package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json

import swopen.jsonToolbox.codec.IgnoreNull


@IgnoreNull()
case class HeaderInternal(
  description: Option[String],
  required: Option[Boolean],
  deprecated: Option[Boolean],
  allowEmptyValue: Option[Boolean],

  style: Option[String],
  explode: Option[Boolean],
  allowReserved: Option[Boolean],
  schema: Option[FullSchema],
  example: Option[Json],
  examples: Option[Map[String,Example|RefTo]],

  content: Option[Map[String, MediaType]]
)
type Header = WithExtensions[HeaderInternal]