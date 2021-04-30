package swopen.openapi

import swopen.jsonToolbox.json.Json

case class SchemaInternal(
  title: Option[String],
  description: Option[String],
  `enum`: Option[Vector[Json]],
  default: Option[Json],
  format: Option[String],
  `type`: Option[SchemaType],

  //array
  items: Option[OrRef[Schema]],
  maxItems: Option[Int],
  minItems: Option[Int],
  uniqueItems: Option[Boolean],

  // string
  pattern: Option[String],
  maxLength: Option[Int],
  minLength: Option[Int],
  //number
  maximum: Option[Int],
  minimum: Option[Int],
  exclusiveMaximum: Option[Int],
  exclusiveMinimum: Option[Int],
  multipleOf: Option[Int],
  // object

  required: Option[Vector[String]],
  // properties: Option[]
)
type Schema = WithExtensions[SchemaInternal]