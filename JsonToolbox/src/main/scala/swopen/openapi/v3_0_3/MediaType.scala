package swopen.openapi.v3_0_3


import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.codec.*


case class MediaTypeInternal(
  schema: Option[FullSchema],
  example: Option[OrRef[Example]],
  examples: Option[Map[String,OrRef[Example]]],
  encoding: Option[Map[String,Encoding]]
) derives Encoder
type MediaType = WithExtensions[MediaTypeInternal]