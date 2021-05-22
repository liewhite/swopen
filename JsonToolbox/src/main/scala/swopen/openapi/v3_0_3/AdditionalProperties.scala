package swopen.openapi.v3_0_3

import swopen.jsonToolbox.codec.*

enum AdditionalProperties derives Encoder:
  case B(b:Boolean)
  case Scm(scm: FullSchema)