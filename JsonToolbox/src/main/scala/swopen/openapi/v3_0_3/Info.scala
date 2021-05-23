package swopen.openapi.v3_0_3


import swopen.jsonToolbox.codec.*


@IgnoreNull()
case class InfoInternal(
  title: String,
  description: Option[String],
  termsOfService: Option[String],
  contact: Option[WithExtensions[Contact]],
  license: Option[WithExtensions[License]],
  version: String,
) derives Encoder, Decoder
type Info = WithExtensions[InfoInternal]