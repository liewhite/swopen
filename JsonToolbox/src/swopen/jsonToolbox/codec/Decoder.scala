package swopen.jsonToolbox.codec

import swopen.jsonToolbox.json.Json

trait Encoder[T]:
  def encode(t:T):Json