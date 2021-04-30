package swopen.openapi.v3_0_3


/**
 *  Server detail
 * */
case class ServerInternal(
  url: String,
  description: Option[String],
  variables: Option[Map[String,ServerVariable]]
)

type Server = WithExtensions[ServerInternal]