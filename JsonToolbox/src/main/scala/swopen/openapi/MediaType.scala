package swopen.openapi


import swopen.jsonToolbox.json.Json

case class MediaTypeInternal(
  schema: Option[OrRef[Schema]],
  example: Option[Json],
  examples: Option[Map[String,OrRef[Example]]],
  encoding: Option[Map[String,Encoding]]
)
type MediaType = WithExtensions[MediaTypeInternal]