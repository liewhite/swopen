package swopen.jsonToolbox.schema

import swopen.openapi.v3_0_3.Schema
import scala.deriving.*
import scala.compiletime.*
import swopen.jsonToolbox.utils.SummonUtils

class SchemaItems[T](val items: List[Schema], val productOrSum: String)


object SchemaItems:
  /**
   * 
   * 编译时会展开summonAll,导致递归summonInline.
   * 如果using SchemaItems的地方是递归数据结构，则会在items里留下null
   * 
   */
  inline given [T](using m:Mirror.Of[T]): SchemaItems[T] = 
    lazy val items = SummonUtils.summonAll[JsonSchema, m.MirroredElemTypes]
    val result = inline m match
      case p:Mirror.ProductOf[T] => 
        SchemaItems[T](items.map(_.schema),"product")
      case s:Mirror.SumOf[T] => SchemaItems[T](items.map(_.schema), "sum")
    result

