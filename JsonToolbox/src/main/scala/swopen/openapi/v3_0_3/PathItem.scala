package swopen.openapi.v3_0_3


/**
 *  paths
 * */
case class PathItemInternal(
  `$ref`: Option[String],
  summary: Option[String],
  description: Option[String],

  get: Option[PathOperation],
  post: Option[PathOperation],
  delete: Option[PathOperation],
  put: Option[PathOperation],
  head: Option[PathOperation],
  patch: Option[PathOperation],
  options: Option[PathOperation],
  trace: Option[PathOperation],

  servers: Option[Vector[Server]],
  parameters: Option[Vector[OrRef[Parameter]]]
)

type PathItem = WithExtensions[PathItemInternal]