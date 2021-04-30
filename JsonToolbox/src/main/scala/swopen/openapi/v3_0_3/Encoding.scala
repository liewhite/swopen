package swopen.openapi.v3_0_3

case class EncodingInternal(
  contentType: Option[String],
  headers: Option[Map[String, OrRef[Header]]],
  style: Option[String],
  explode: Option[String],
  allowReserved: Option[Boolean] 
)
type Encoding = WithExtensions[EncodingInternal]