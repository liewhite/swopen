package swopen.jsonToolbox.constraint

import swopen.jsonToolbox.json.Json

trait TValidator:
  def validate(json:Json): Boolean

class Validator(enumValues: Vector[Json]) extends scala.annotation.Annotation with TValidator:
  def validate(json:Json): Boolean = true