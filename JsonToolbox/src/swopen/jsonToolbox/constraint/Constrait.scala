package swopen.jsonToolbox.constraint

import swopen.jsonToolbox.json.Json

// 统一取 Constrait annotation， 然后根据field type 转换类型，转换失败抛出错误
// 每一个field只有一个 Constraint annotation
class Constraint(val enumValues: Vector[Json]) extends scala.annotation.Annotation