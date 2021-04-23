package swopen.jsonToolbox

import scala.deriving.*
import scala.compiletime.*
import shapeless3.deriving.*

import swopen.jsonToolbox.json.Json

// 只针对参数,外层有 routes， 内层有参数列表
trait JsonBehavior[T]:
  def schema:Schema
  def Encode[T](t:T):Json = ???
  def Decode[T](s:Json):T = ???


object JsonBehavior:
  def schema[T](using o:JsonBehavior[T]):Schema = o.schema

  given [T](using value: JsonBehavior[T]):JsonBehavior[Map[String,T]] =
    new JsonBehavior[Map[String,T]]:
      def schema:Schema = Schema.OMap(value.schema)

  given [T](using value: JsonBehavior[T]):JsonBehavior[List[T]] =
    new JsonBehavior[List[T]]:
      def schema:Schema = Schema.OArray(value.schema)

  given [T](using value: JsonBehavior[T]):JsonBehavior[Vector[T]] =
    new JsonBehavior[Vector[T]]:
      def schema:Schema = Schema.OArray(value.schema)

  given [T](using value: JsonBehavior[T]):JsonBehavior[Seq[T]] =
    new JsonBehavior[Seq[T]]:
      def schema:Schema = Schema.OArray(value.schema)

  given [T](using value: JsonBehavior[T]):JsonBehavior[Array[T]] =
    new JsonBehavior[Array[T]]:
      def schema:Schema = Schema.OArray(value.schema)

  given JsonBehavior[Int] with
    def schema:Schema = Schema.OInt32

  given JsonBehavior[Long] with
    def schema:Schema = Schema.OInt64

  given JsonBehavior[Float] with
    def schema:Schema = Schema.OFloat32

  given JsonBehavior[Double] with
    def schema:Schema = Schema.OFloat64

  given JsonBehavior[Boolean] with
    def schema:Schema = Schema.OBoolean

  given JsonBehavior[String] with
    def schema:Schema = Schema.OString

  given JsonBehavior[Array[Byte]] with
    def schema:Schema = Schema.OBytes

  inline def summonAll[T <: Tuple]: List[Schema] = 
    inline erasedValue[T] match
        case _: (t *: ts) => summonInline[JsonBehavior[t]].schema :: summonAll[ts]
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

  inline given derived[T](using m: Mirror.Of[T]): JsonBehavior[T] = 
    val items = summonAll[m.MirroredElemTypes]
    val labels = summonValues[m.MirroredElemLabels]
    val schemas = inline m match
          case s: Mirror.SumOf[T]     => 
              sum(items)
          case p: Mirror.ProductOf[T] => 
              product(items,tuple2List(labels).toVector)

    new JsonBehavior[T]:
      def schema =  schemas

end JsonBehavior

