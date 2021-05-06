package swopen.openapi.v3_0_3


import swopen.jsonToolbox.json.Json

case class MediaTypeInternal(
  schema: Option[Schema],
  example: Option[OrRef[Example]],
  examples: Option[Map[String,OrRef[Example]]],
  encoding: Option[Map[String,Encoding]]
)
type MediaType = WithExtensions[MediaTypeInternal]