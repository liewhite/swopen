package swopen.jsonToolbox.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.JsonInclude.Include


object Stringify:
  val mapper = ObjectMapper()
  mapper.setSerializationInclusion(Include.NON_NULL)