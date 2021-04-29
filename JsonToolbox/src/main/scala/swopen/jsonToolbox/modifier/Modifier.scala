package swopen.jsonToolbox.modifier

import shapeless3.deriving.Annotation
import scala.util.NotGiven
import swopen.jsonToolbox.json.Json


// encoder -> modifier -> validator -> serializer
// deserializer -> validator -> modifier -> decoder
class Modifier(val rename: String) extends scala.annotation.Annotation
