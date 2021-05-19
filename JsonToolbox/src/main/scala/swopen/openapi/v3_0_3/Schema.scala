package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json

case class SchemaInternal(
  // same with json schema
  // title: Option[String] = None,
  // multipleOf: Option[Int] = None,
  // maximum: Option[Int] = None,
  // exclusiveMaximum: Option[Int] = None,
  // minimum: Option[Int] = None,
  // exclusiveMinimum: Option[Int] = None,
  // maxLength: Option[Int] = None,
  // minLength: Option[Int] = None,
  // pattern: Option[String] = None,
  // maxItems: Option[Int] = None,
  // minItems: Option[Int] = None,
  // uniqueItems: Option[Boolean] = None,
  // maxProperties: Option[Int] = None,
  // minProperties: Option[Int] = None,
  // required: Option[Vector[String]] = None,
  // `enum`: Option[Vector[Json]] = None,
  // const: Option[Json] = None,
  // changed from json schema
  // `type`: Option[SchemaType] = None,
  allOf: Option[Vector[Schema]] = None,
  oneOf: Option[Vector[Schema]] = None,
  anyOf: Option[Vector[Schema]] = None,
  not: Option[Schema] = None,
  items: Option[Schema] = None,
  properties: Option[WithExtensions[Map[String, Schema]]] = None,
  additionalProperties: Option[AdditionalProperties] = None,
  // description: Option[String] = None,
  // format: Option[String] = None,
  // default: Option[Json] = None,

  // new in openapi, not support yet
)
type Schema = OrRef[WithExtensions[SchemaInternal]]