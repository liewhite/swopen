package swopen.jsonToolbox.typeclasses

import io.swagger.v3.oas.models.media.Schema

trait TJsonSchema[T]:
  def schema: Schema[Any]

object TJsonSchema:
  ???