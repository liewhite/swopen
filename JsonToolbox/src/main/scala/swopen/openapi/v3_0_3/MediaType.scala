package swopen.openapi.v3_0_3


import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.codec.*


import swopen.jsonToolbox.codec.*


@IgnoreNull()
case class MediaTypeInternal(
  schema: Option[FullSchema],
  example: Option[Example|RefTo],
  examples: Option[Map[String,Example|RefTo]],
  encoding: Option[Map[String,Encoding]]
) derives Encoder,Decoder
type MediaType = WithExtensions[MediaTypeInternal]