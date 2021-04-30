package swopen.openapi


/**
 *  paths
 * */
case class PathItem(
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