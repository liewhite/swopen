package swopen.openapi


import swopen.jsonToolbox.json.Json

case class ExampleInternal(
  summary: Option[String],
  description: Option[String],
  externalValue: Option[String],
  value: Option[Json]
)

type Example = WithExtensions[ExampleInternal]