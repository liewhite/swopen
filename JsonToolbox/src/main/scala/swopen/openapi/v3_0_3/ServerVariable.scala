package swopen.openapi.v3_0_3


case class ServerVariableInternal(
  `enum`: Option[Vector[String]],
  default: String,
  description: Option[String]
)

type ServerVariable = WithExtensions[ServerVariableInternal]