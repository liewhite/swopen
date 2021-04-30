package swopen.openapi


case class ServerVariable(
  `enum`: Option[Vector[String]],
  default: String,
  description: Option[String]
)