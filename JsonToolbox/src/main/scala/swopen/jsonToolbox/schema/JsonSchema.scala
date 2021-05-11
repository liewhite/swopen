package swopen.jsonToolbox.schema

import java.math.{BigDecimal,BigInteger}
import scala.math.{BigDecimal as ScalaBigDecimal,BigInt}
import scala.deriving.*
import scala.collection.concurrent
import scala.compiletime.*
import shapeless3.deriving.*

import swopen.jsonToolbox.json.Json
import swopen.openapi.v3_0_3.*
// import swopen.jsonToolbox.schema.{Schema,SchemaNumberImpl}
import swopen.jsonToolbox.codec.{Encoder,Decoder}
import swopen.jsonToolbox.utils.SummonUtils

// 
// case class FullSchema(component: Map[String, Schema], schema:Schema)
/**
 *  负责json 的序列化， 反序列化， jsonSchema生成， 以及validation
 **/
trait JsonSchema[T]:
  def schema: Schema
end JsonSchema

object JsonSchema:
  // 需要线程安全
  val component: concurrent.Map[String, WithExtensions[SchemaInternal]] = concurrent.TrieMap.empty

  def schema[T](using o:JsonSchema[T]):Schema = o.schema

  given [T](using value: JsonSchema[T]):JsonSchema[Map[String,T]] =
    new JsonSchema[Map[String,T]]:
      def schema: Schema = 
        OrRef.Id(WithExtensions(SchemaInternal(
          `type` = Some(SchemaType.`object`),
          additionalProperties = Some(AdditionalProperties.Scm(value.schema))
          )))

  given [T](using value: JsonSchema[T]):JsonSchema[List[T]] =
    new JsonSchema[List[T]]:
      def schema:Schema = 
        OrRef.Id(WithExtensions(SchemaInternal(
          `type` = Some(SchemaType.array),
          items = Some(value.schema)
          )))

  given [T](using value: JsonSchema[T]):JsonSchema[Vector[T]] =
    new JsonSchema[Vector[T]]:
      def schema:Schema =
        OrRef.Id(WithExtensions(SchemaInternal(
          `type` = Some(SchemaType.array),
          items = Some(value.schema)
          )))

  given [T](using value: JsonSchema[T]):JsonSchema[Seq[T]] =
    new JsonSchema[Seq[T]]:
      def schema:Schema =
        OrRef.Id(WithExtensions(SchemaInternal(
          `type` = Some(SchemaType.array),
          items = Some(value.schema)
          )))

  given [T](using value: JsonSchema[T]):JsonSchema[Array[T]] =
    new JsonSchema[Array[T]]:
      def schema:Schema =
        OrRef.Id(WithExtensions(SchemaInternal(
          `type` = Some(SchemaType.array),
          items = Some(value.schema)
          )))

  given JsonSchema[Int] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.number),
        )))

  given JsonSchema[Long] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.number),
        )))

  given JsonSchema[Float] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.number),
        )))

  given JsonSchema[Double] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.number),
        )))

  given JsonSchema[BigInteger] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.number),
        )))

  given JsonSchema[BigInt] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.number),
        )))

  given JsonSchema[BigDecimal] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.number),
        )))

  given JsonSchema[ScalaBigDecimal] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.number),
        )))

  given JsonSchema[Boolean] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.boolean),
        )))

  given JsonSchema[String] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.string),
        )))

  // given JsonSchema[Array[Byte]] with
  //   def schema:Schema = Schema.SchemaBytes

  given JsonSchema[Json] with
    def schema:Schema =
      OrRef.Id(WithExtensions(SchemaInternal(
        )))

  inline def summonAll[T <: Tuple]: List[Schema] = 
    inline erasedValue[T] match
        case _: (t *: ts) => summonInline[JsonSchema[t]].schema :: summonAll[ts]
        case _: EmptyTuple => Nil

  def schemaDeref(schema:Schema):WithExtensions[SchemaInternal] = 
    schema match
      case OrRef.Id(s) => s
      case OrRef.Ref(ref) => component(ref)

  // 真的one of， 或者enum
  def sum(element: List[Schema]): Schema = 
    if element.find(item => 
      val schema = schemaDeref(item).spec
      schema.`type` != SchemaType.string
    ).isEmpty then
      val es = element.map(item =>
        val schema = schemaDeref(item).spec
        schema.const.get
      ).toVector
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.string),
        `enum` = Some(es)
        )))
    else
      OrRef.Id(WithExtensions(SchemaInternal(
        oneOf = Some(element.toVector),
        )))
  
  def product(elements:List[Schema],label:String, labels:Vector[String], defaults:Map[String,Any] /*todo annotations 放这里*/): Schema = 
    val itemKeys = labels
    if itemKeys.isEmpty then
      OrRef.Id(WithExtensions(SchemaInternal(
        `type` = Some(SchemaType.string),
        const = Some(Json.JString(label))
        )))
    else
      val schema = SchemaInternal(
        `type` = Some(SchemaType.`object`),
        properties = Some(WithExtensions(labels.zip(elements).toMap))
      )
      OrRef.Id(WithExtensions(schema))
  

  def tuple2List[T<: Tuple](data:T): List[String] = 
    data match 
      case h *: t => h.asInstanceOf[String] :: tuple2List(t)
      case EmptyTuple => Nil

  inline given derived[T](using labelling:Labelling[T], q:QualifiedName[T])(using m: Mirror.Of[T]): JsonSchema[T] = 
    val name = q.fullName
    val schemaObj = 
      if !component.contains(name) then
        // 占位
        component.addOne(name,null)
        val items = SummonUtils.summonAll[JsonSchema, m.MirroredElemTypes].map(_.schema)
        val labels = labelling.elemLabels.toVector
        val label = labelling.label
        val schemas = inline m match
          case s: Mirror.SumOf[T]     => 
              sum(items)
          case p: Mirror.ProductOf[T] => 
              val defaults = summon[DefaultValue[T]]
              product(items,label,labels, defaults.defaults)
        schemas match
          case (OrRef.Id(s)) =>
            component.addOne(name,s)
            OrRef.Ref(name)
          case other => throw Exception(s"schema error, expect OrRef.Id,got: ${other}")
        schemas
      else
        OrRef.Ref(name)
    
    println(s"after schemas, $name")
    new JsonSchema[T]:
      def schema = schemaObj

end JsonSchema

