package swopen.jsonToolbox


enum Schema:
  case OObject(items: Vector[(ItemKey, Schema)])
  case OMap(valueSchema: Schema)
  case OArray(itemSchema: Schema)
  case OInt32
  case OInt64
  case OFloat32
  case OFloat64
  case OBoolean
  case OString
  case OBytes
  case OUnion(itemSchemas: Vector[Schema])