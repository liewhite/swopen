package swopen.jsonSchema

import scala.deriving.*
import scala.compiletime.*
import shapeless3.deriving.*

// TODO validations
sealed trait Validator
case class VType()


case class ItemKey(name:String) // 这里应包含所有field的元数据,包括 required， 

enum Schema:
  case OObject(items: Vector[(ItemKey, Schema)])
  case OMap(valueSchema: Schema)
  case OArray(itemSchema: Schema)
  case OInt32
  case OInt64
  case OFloat32
  case OFloat64
  case OBoolean
  case OString
  case OBytes
  case OUnion(itemSchemas: Vector[Schema])

// 只针对参数,外层有 routes， 内层有参数列表
trait JsonSchema[T]:
  def schema:Schema


object JsonSchema:
  def schema[T](using o:JsonSchema[T]):Schema = o.schema

  given [T](using value: JsonSchema[T]):JsonSchema[Map[String,T]] =
    new JsonSchema[Map[String,T]]:
      def schema:Schema = Schema.OMap(value.schema)

  given [T](using value: JsonSchema[T]):JsonSchema[List[T]] =
    new JsonSchema[List[T]]:
      def schema:Schema = Schema.OArray(value.schema)

  given [T](using value: JsonSchema[T]):JsonSchema[Vector[T]] =
    new JsonSchema[Vector[T]]:
      def schema:Schema = Schema.OArray(value.schema)

  given [T](using value: JsonSchema[T]):JsonSchema[Seq[T]] =
    new JsonSchema[Seq[T]]:
      def schema:Schema = Schema.OArray(value.schema)

  given [T](using value: JsonSchema[T]):JsonSchema[Array[T]] =
    new JsonSchema[Array[T]]:
      def schema:Schema = Schema.OArray(value.schema)

  given JsonSchema[Int] with
    def schema:Schema = Schema.OInt32

  given JsonSchema[Long] with
    def schema:Schema = Schema.OInt64

  given JsonSchema[Float] with
    def schema:Schema = Schema.OFloat32

  given JsonSchema[Double] with
    def schema:Schema = Schema.OFloat64

  given JsonSchema[Boolean] with
    def schema:Schema = Schema.OBoolean

  given JsonSchema[String] with
    def schema:Schema = Schema.OString

  given JsonSchema[Array[Byte]] with
    def schema:Schema = Schema.OBytes

  inline def summonAll[T <: Tuple]: List[Schema] = 
    inline erasedValue[T] match
        case _: (t *: ts) => summonInline[JsonSchema[t]].schema :: summonAll[ts]
        case _: EmptyTuple => Nil

  def sum(element: List[Schema]): Schema = 
      Schema.OUnion(element.toVector)
  
  def product(elements:List[Schema], labels:Vector[String] /*todo annotations 放这里*/): Schema = 
    val itemKeys = labels.map(ItemKey(_))
    Schema.OObject(itemKeys.zip(elements).toVector)
  

  def tuple2List[T<: Tuple](data:T): List[String] = 
    data match 
      case h *: t => h.asInstanceOf[String] :: tuple2List(t)
      case EmptyTuple => Nil

  inline given [T](using m: Mirror.Of[T]): JsonSchema[T] = 
    val items = summonAll[m.MirroredElemTypes]
    val labels = summonValues[m.MirroredElemLabels]
    val schemas = inline m match
          case s: Mirror.SumOf[T]     => 
              sum(items)
          case p: Mirror.ProductOf[T] => 
              product(items,tuple2List(labels).toVector)

    new JsonSchema[T]:
      def schema =  schemas

end JsonSchema

