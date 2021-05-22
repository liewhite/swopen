package swopen.openapi.v3_0_3

import swopen.jsonToolbox.codec.Encoder
import swopen.jsonToolbox.json.Json

case class SchemaInternal(
  // same with json schema
  title: Option[String] = None,
  multipleOf: Option[Int] = None,
  maximum: Option[Int] = None,
  exclusiveMaximum: Option[Int] = None,
  minimum: Option[Int] = None,
  exclusiveMinimum: Option[Int] = None,
  maxLength: Option[Int] = None,
  minLength: Option[Int] = None,
  pattern: Option[String] = None,
  maxItems: Option[Int] = None,
  minItems: Option[Int] = None,
  uniqueItems: Option[Boolean] = None,
  maxProperties: Option[Int] = None,
  minProperties: Option[Int] = None,
  required: Option[Vector[String]] = None,
  // comment one of the two lines below, compile pass
  `enum`: Option[Vector[Json]] = None,
  properties: Option[WithExtensions[Map[String, FullSchema]]] = None,
  const: Option[Json] = None,
  // changed from json schema
  `type`: Option[SchemaType] = None,
  allOf: Option[Vector[FullSchema]] = None,
  oneOf: Option[Vector[FullSchema]] = None,
  anyOf: Option[Vector[FullSchema]] = None,
  not: Option[FullSchema] = None,
  items: Option[FullSchema] = None,
  additionalProperties: Option[AdditionalProperties] = None,
  description: Option[String] = None,
  format: Option[String] = None,
  default: Option[Json] = None,

  // new in openapi, not support yet
) 

type FullSchema = OrRef[WithExtensions[SchemaInternal]]
object FullSchema:
  def apply(schema: SchemaInternal) = OrRef.Id(WithExtensions(schema))