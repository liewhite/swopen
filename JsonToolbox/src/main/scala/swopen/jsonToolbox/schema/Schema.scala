package swopen.jsonToolbox.schema


import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.schema.ItemKey

/**
 *  json schema validation
 * 
 *  validate(data: Json, schema: Schema): Option[Exception]
 *  或者 schema.validate(json)
 * 
 * */

enum SchemaNumberImpl:
  case SchemaInt
  case SchemaLong
  case SchemaFloat
  case SchemaDouble
  case SchemaBigInt
  case SchemaBigDecimal

// TODO constraint 应该加在value上
enum Schema:
  case SchemaObject(items: Vector[(ItemKey, Schema)])
  case SchemaMap(valueSchema: Schema)
  case SchemaArray(itemSchema: Schema)
  case SchemaNumber(impl: SchemaNumberImpl)
  case SchemaBoolean
  case SchemaString
  case SchemaBytes
  case SchemaUnion(itemSchemas: Vector[Schema])

  def validate(data:Json): Option[Exception] = 
    this match 
      case SchemaObject(constraint) =>  // items and constraint
        ???
      case _ => ???

  