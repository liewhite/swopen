package swopen.openapi.v3_0_3


/**
 *  Server detail
 * */
import swopen.jsonToolbox.codec.*

@IgnoreNull()
case class ServerInternal(
  url: String,
  description: Option[String],
  variables: Option[Map[String,ServerVariable]]
) derives Encoder,Decoder

type Server = WithExtensions[ServerInternal]