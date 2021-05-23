package swopen.openapi.v3_0_3


import swopen.jsonToolbox.codec.*


@IgnoreNull()
case class License(
  name: String,
  url: Option[String]
) derives Encoder,Decoder