package swopen.openapi.v3_0_3

import swopen.jsonToolbox.codec.*

case class Components(
  schemas: Option[Map[String, FullSchema]]
) derives Encoder