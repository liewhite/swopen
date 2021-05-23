package swopen.openapi.v3_0_3


import swopen.jsonToolbox.codec.IgnoreNull


@IgnoreNull()
case class EncodingInternal(
  contentType: Option[String],
  headers: Option[Map[String, Header | RefTo]],
  style: Option[String],
  explode: Option[String],
  allowReserved: Option[Boolean] 
)
type Encoding = WithExtensions[EncodingInternal]