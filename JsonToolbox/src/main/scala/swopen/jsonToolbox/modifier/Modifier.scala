package swopen.jsonToolbox.modifier

import shapeless3.deriving.Annotation
import scala.util.NotGiven
import swopen.jsonToolbox.json.Json


trait TModifier:
  def modify(json:Json):Json
// encoder -> modifier -> validator -> serializer
// deserializer -> validator -> modifier -> decoder
class Modifier(val rename: String) extends scala.annotation.Annotation with TModifier:
  def modify(json:Json):Json = 
    json
