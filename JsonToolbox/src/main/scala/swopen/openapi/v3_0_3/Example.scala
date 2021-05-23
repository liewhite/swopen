package swopen.openapi.v3_0_3


import swopen.jsonToolbox.json.Json

import swopen.jsonToolbox.codec.IgnoreNull


@IgnoreNull()
case class ExampleInternal(
  summary: Option[String],
  description: Option[String],
  externalValue: Option[String],
  value: Option[Json]
)

type Example = WithExtensions[ExampleInternal]