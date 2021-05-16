package swopen.jsonToolbox.schema

import java.math.{BigDecimal,BigInteger}
import scala.math.{BigDecimal as ScalaBigDecimal,BigInt}
import scala.deriving.*
import scala.collection.concurrent
import scala.compiletime.*
import scala.util.NotGiven
import scala.quoted.*
import shapeless3.deriving.*

import swopen.jsonToolbox.json.Json
import swopen.openapi.v3_0_3.*
// import swopen.jsonToolbox.schema.{Schema,SchemaNumberImpl}
import swopen.jsonToolbox.codec.{Encoder,Decoder}
import swopen.jsonToolbox.utils.SummonUtils

trait MacroSchema

object MacroSchema:
  inline given [T](using NotGiven[JsonSchema[T]]): JsonSchema[T] = ${ MacroSchema.impl[T] }

  def impl[T:Type](using q: Quotes): Expr[JsonSchema[T]] = 
    import q.reflect._

    val repr = TypeRepr.of[T];
    repr match
      case OrType(a,b) => 
        (a.asType,b.asType) match
          case ('[t1],'[t2]) => 
            '{new JsonSchema[T] {
              def schema = 
                val o1 = summonInline[JsonSchema[t1]]
                val o2 = summonInline[JsonSchema[t2]]
                OrRef.Id(WithExtensions(SchemaInternal(
                  oneOf = Some(Vector(o1.schema,o2.schema))
                )))
            }
            }
      // case other => 
      //   other.asType match
      //     case '[t] =>
      //       '{new JsonSchema[T] {
      //         def schema(data:T) = 
      //           // this won't occur a recusively call, but i dont know why
      //           val o1 = summonInline[JsonSchema[t]].asInstanceOf[JsonSchema[T]]
      //           o1.schema
      //       }
      //       }
/**
 *  负责json 的序列化， 反序列化， jsonSchema生成， 以及validation
 **/
trait JsonSchema[T] extends MacroSchema:
  def schema: Schema
end JsonSchema

object JsonSchema:
  // 需要线程安全
  val component: concurrent.Map[String, WithExtensions[SchemaInternal]] = concurrent.TrieMap.empty

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

  given [T](using value: => JsonSchema[T]):JsonSchema[Option[T]] =
    new JsonSchema[Option[T]]:
      def schema:Schema =
        value.schema

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
      case OrRef.Ref(ref) => 
        println(component(ref))
        component(ref)

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

  
  /**
   * 如果当前component里不存在该类型的 schema， 则生成一个
   * 返回 OrRef.ref
   * 
   * SchemaItems 需要summon mirror.elementTypes 的JsonSchema
   **/
  given [T](using labelling:Labelling[T], q:QualifiedName[T],m: => SchemaItems[T]): JsonSchema[T] = 
    new JsonSchema[T]:
      def schema = 
        val schemaObj = 
          val name = q.fullName
          if !component.contains(name) then
            // 第一时间占位， 下次进入product分支就直接返回ref了
            component.addOne(name,null)
            val items = m.items
            val defaults = summon[DefaultValue[T]]
            val labels = labelling.elemLabels.toVector
            val label = labelling.label

            val schemas = 
              if m.productOrSum == "product" then
                product(items,label,labels, defaults.defaults)
              else
                sum(items)
            schemas
          else
            OrRef.Ref(name)
        schemaObj

end JsonSchema
