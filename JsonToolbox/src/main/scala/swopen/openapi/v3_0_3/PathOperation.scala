package swopen.openapi.v3_0_3


import swopen.jsonToolbox.codec.IgnoreNull

@IgnoreNull()
case class PathOperationInternal()
type PathOperation = WithExtensions[PathOperationInternal]