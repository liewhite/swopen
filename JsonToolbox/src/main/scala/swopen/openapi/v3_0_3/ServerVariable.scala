package swopen.openapi.v3_0_3


import swopen.jsonToolbox.codec.*

@IgnoreNull()
case class ServerVariableInternal(
  `enum`: Option[Vector[String]],
  default: String,
  description: Option[String]
) derives Encoder,Decoder

type ServerVariable = WithExtensions[ServerVariableInternal]