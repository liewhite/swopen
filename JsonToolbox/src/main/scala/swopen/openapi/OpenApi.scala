package swopen.openapi


case class OpenApi(
  openapi: String,
  info: OAInfo,
  paths: Map[String, OAPath]
)

case class OAInfo(
  title:String,
  version:String
)

case class OAPath(
  summary: Option[String],
  description: Option[String],
  
)