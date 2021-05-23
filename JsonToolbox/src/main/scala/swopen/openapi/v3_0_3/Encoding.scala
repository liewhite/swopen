package swopen.openapi.v3_0_3


import swopen.jsonToolbox.codec.*


@IgnoreNull()
case class EncodingInternal(
  contentType: Option[String],
  headers: Option[Map[String, Header | RefTo]],
  style: Option[String],
  explode: Option[String],
  allowReserved: Option[Boolean] 
) derives Encoder, Decoder
type Encoding = WithExtensions[EncodingInternal]