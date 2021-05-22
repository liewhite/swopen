package swopen.openapi.v3_0_3
import swopen.jsonToolbox.codec.*

enum SchemaType derives Encoder:
  case array	
  case boolean
  case integer
  case number
  case `object`
  case string