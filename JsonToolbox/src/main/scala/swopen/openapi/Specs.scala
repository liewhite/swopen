package swopen.openapi


case class OpenApiRoot(
  openapi: String,
  info: OpenApiObject[OpenApiInfo], 
  servers: Option[Vector[OpenApiObject[OpenApiServer]]],
  paths: Vector[OpenApiObject[Map[String,OpenApiPath]]],
  components: Option[OpenApiObject[OpenApiComponents]],
  security: Option[Vector[OpenApiObject[OpenApiSecurity]]],
  tags: Option[Vector[OpenApiObject[OpenApiTag]]],
  externalDocs: Option[Vector[OpenApiObject[OpenApiExternalDoc]]],
)

/**
 *  Info detail
 * */
case class OpenApiInfo(
  title: String,
  description: Option[String],
  termsOfService: Option[String],
  contact: Option[OpenApiObject[OpenApiInfoContact]],
  license: Option[OpenApiObject[OpenApiInfoLicense]],
  version: String,
)
case class OpenApiInfoContact(
  name: Option[String],
  url: Option[String],
  email: Option[String],
)

case class OpenApiInfoLicense(
  name: String,
  url: Option[String]
)

/**
 *  Server detail
 * */
case class OpenApiServer(
  url: String,
  description: Option[String],
  variables: Option[Map[String,OpenApiObject[OpenApiServerVariable]]]
)

case class OpenApiServerVariable(
  `enum`: Option[Vector[String]],
  default: String,
  description: Option[String]
)

/**
 *  paths
 * */
case class OpenApiPath(
  `$ref`: Option[String],
  summary: Option[String],
  description: Option[String],

  get: Option[OpenApiObject[OpenApiPathOperation]],
  post: Option[OpenApiObject[OpenApiPathOperation]],
  delete: Option[OpenApiObject[OpenApiPathOperation]],
  put: Option[OpenApiObject[OpenApiPathOperation]],
  head: Option[OpenApiObject[OpenApiPathOperation]],
  patch: Option[OpenApiObject[OpenApiPathOperation]],
  options: Option[OpenApiObject[OpenApiPathOperation]],
  trace: Option[OpenApiObject[OpenApiPathOperation]],

  servers: Option[Vector[OpenApiObject[OpenApiServer]]],
  parameters: Option[Vector[OpenApiObject[Parameter]]]
)

enum Parameter:
  case ParameterObject()
  case ReferenceObject()

case class OpenApiPathOperation()
case class OpenApiComponents()
case class OpenApiSecurity()
case class OpenApiTag()
case class OpenApiExternalDoc()