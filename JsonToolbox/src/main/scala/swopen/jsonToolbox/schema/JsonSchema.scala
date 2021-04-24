package swopen.jsonToolbox.schema

import java.math.{BigDecimal,BigInteger}
import scala.deriving.*
import scala.compiletime.*
import shapeless3.deriving.*

import swopen.jsonToolbox.json.Json
import swopen.jsonToolbox.schema.{Schema,SchemaNumberImpl}
import swopen.jsonToolbox.codec.{Encoder,Decoder}

/**
 *  负责json 的序列化， 反序列化， jsonSchema生成， 以及validation
 **/
trait JsonSchema[T]:
  def schema: Schema
  // def encoder: Encoder[T]
  // def decoder: Decoder[T]
end JsonSchema

object JsonSchema:
  def schema[T](using o:JsonSchema[T]):Schema = o.schema

  given [T](using value: JsonSchema[T]):JsonSchema[Map[String,T]] =
    new JsonSchema[Map[String,T]]:
      def schema: Schema = Schema.SchemaMap(value.schema)

  given [T](using value: JsonSchema[T]):JsonSchema[List[T]] =
    new JsonSchema[List[T]]:
      def schema:Schema = Schema.SchemaArray(value.schema)

  given [T](using value: JsonSchema[T]):JsonSchema[Vector[T]] =
    new JsonSchema[Vector[T]]:
      def schema:Schema = Schema.SchemaArray(value.schema)

  given [T](using value: JsonSchema[T]):JsonSchema[Seq[T]] =
    new JsonSchema[Seq[T]]:
      def schema:Schema = Schema.SchemaArray(value.schema)

  given [T](using value: JsonSchema[T]):JsonSchema[Array[T]] =
    new JsonSchema[Array[T]]:
      def schema:Schema = Schema.SchemaArray(value.schema)

  given JsonSchema[Int] with
    def schema:Schema = Schema.SchemaNumber(SchemaNumberImpl.SchemaInt)

  given JsonSchema[Long] with
    def schema:Schema = Schema.SchemaNumber(SchemaNumberImpl.SchemaLong)

  given JsonSchema[Float] with
    def schema:Schema = Schema.SchemaNumber(SchemaNumberImpl.SchemaFloat)

  given JsonSchema[Double] with
    def schema:Schema = Schema.SchemaNumber(SchemaNumberImpl.SchemaDouble)

  given JsonSchema[BigInteger] with
    def schema:Schema = Schema.SchemaNumber(SchemaNumberImpl.SchemaBigInt)

  given JsonSchema[BigDecimal] with
    def schema:Schema = Schema.SchemaNumber(SchemaNumberImpl.SchemaBigDecimal)

  given JsonSchema[Boolean] with
    def schema:Schema = Schema.SchemaBoolean

  given JsonSchema[String] with
    def schema:Schema = Schema.SchemaString

  given JsonSchema[Array[Byte]] with
    def schema:Schema = Schema.SchemaBytes

  inline def summonAll[T <: Tuple]: List[Schema] = 
    inline erasedValue[T] match
        case _: (t *: ts) => summonInline[JsonSchema[t]].schema :: summonAll[ts]
        case _: EmptyTuple => Nil

  def sum(element: List[Schema]): Schema = 
      Schema.SchemaUnion(element.toVector)
  
  def product(elements:List[Schema], labels:Vector[String] /*todo annotations 放这里*/): Schema = 
    val itemKeys = labels.map(ItemKey(_))
    Schema.SchemaObject(itemKeys.zip(elements).toVector)
  

  def tuple2List[T<: Tuple](data:T): List[String] = 
    data match 
      case h *: t => h.asInstanceOf[String] :: tuple2List(t)
      case EmptyTuple => Nil

  inline given derived[T](using m: Mirror.Of[T]): JsonSchema[T] = 
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

