package swopen.jsonToolbox.codec

import swopen.jsonToolbox.json.Json

trait Decoder[T]:
  def decode(data:Json):T