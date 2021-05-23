package swopen.openapi.v3_0_3

import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.codec.{Encoder,Decoder, DecodeException}

import swopen.jsonToolbox.codec.IgnoreNull

@IgnoreNull()
case class RefTo(`$ref`: String)