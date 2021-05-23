package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.JsonBehavior.*

import swopen.jsonToolbox.codec.*

@IgnoreNull()
case class RefTo(`$ref`: String) derives Encoder,Decoder