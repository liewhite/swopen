package swopen.jsonToolbox

import scala.annotation.Annotation

import swopen.jsonToolbox.json.Json

case class ItemRequired() extends Annotation

case class ItemEnum(values : Vector[Json]) extends Annotation
