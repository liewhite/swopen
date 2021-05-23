package swopen.openapi.v3_0_3

import swopen.jsonToolbox.codec.IgnoreNull


@IgnoreNull()
case class Contact(
  name: Option[String],
  url: Option[String],
  email: Option[String],
)