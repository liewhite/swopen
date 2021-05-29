package swopen.jsonToolbox.modifier

import scala.util.NotGiven
import com.fasterxml.jackson.databind.JsonNode


trait TModifier:
  def modify(json:JsonNode):JsonNode
// encoder -> modifier -> validator -> serializer
// deserializer -> validator -> modifier -> decoder
class Modifier(val rename: String) extends scala.annotation.Annotation with TModifier:
  def modify(json:JsonNode):JsonNode = 
    json
