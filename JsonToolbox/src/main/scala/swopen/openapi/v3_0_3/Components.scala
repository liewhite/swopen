package swopen.openapi.v3_0_3

import swopen.jsonToolbox.codec.*

@IgnoreNull()
case class Components(
  schemas: Option[Map[String, FullSchema]]
) derives Encoder,Decoder