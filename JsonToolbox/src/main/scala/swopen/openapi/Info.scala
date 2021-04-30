package swopen.openapi


case class InfoInternal(
  title: String,
  description: Option[String],
  termsOfService: Option[String],
  contact: Option[WithExtensions[Contact]],
  license: Option[WithExtensions[License]],
  version: String,
)
type Info = WithExtensions[InfoInternal]