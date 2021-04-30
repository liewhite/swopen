package swopen.openapi


/**
 *  Server detail
 * */
case class ServerInternal(
  url: String,
  description: Option[String],
  variables: Option[Map[String,WithExtensions[ServerVariable]]]
)

type Server = WithExtensions[ServerInternal]