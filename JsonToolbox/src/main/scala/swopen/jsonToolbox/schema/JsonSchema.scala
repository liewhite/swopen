// package swopen.jsonToolbox.schema

// import java.math.{BigDecimal,BigInteger}
// import scala.math.{BigDecimal as ScalaBigDecimal,BigInt}
// import scala.deriving.*
// import scala.collection.concurrent
// import scala.compiletime.*
// import scala.util.NotGiven
// import scala.quoted.*

// import swopen.jsonToolbox.json.Json
// import swopen.openapi.v3_0_3.*
// import swopen.jsonToolbox.codec.{Encoder,Decoder}

// import shapeless3.deriving.*

// trait MacroSchema

// object MacroSchema:
//   inline given [T](using NotGiven[JsonSchema[T]]): JsonSchema[T] = ${ MacroSchema.impl[T] }

//   def impl[T:Type](using q: Quotes): Expr[JsonSchema[T]] = 
//     import q.reflect._

//     val repr = TypeRepr.of[T];
//     repr match
//       case OrType(a,b) => 
//         (a.asType,b.asType) match
//           case ('[t1],'[t2]) => 
//             '{new JsonSchema[T] {
//               def schema = 
//                 val o1 = summonInline[JsonSchema[t1]]
//                 val o2 = summonInline[JsonSchema[t2]]
//                 WithExtensions(SchemaInternal(
//                   oneOf = Some(Vector(o1.schema,o2.schema))
//                 ))
//             }
//             }
//       case other => 
//         report.error(s"not support type:,$other");???

// trait JsonSchema[T] extends MacroSchema:
//   def schema: FullSchema
// end JsonSchema

// object JsonSchema:
//   // 需要线程安全
//   val component: concurrent.Map[String, WithExtensions[SchemaInternal]] = concurrent.TrieMap.empty

//   given [T](using value: JsonSchema[T]):JsonSchema[Map[String,T]] =
//     new JsonSchema[Map[String,T]]:
//       def schema: FullSchema = 
//         WithExtensions(SchemaInternal(
//           `type` = Some(SchemaType.`object`),
//           additionalProperties = Some(value.schema)
//           ))

//   given [T](using value: JsonSchema[T]):JsonSchema[List[T]] =
//     new JsonSchema[List[T]]:
//       def schema: FullSchema = 
//         WithExtensions(SchemaInternal(
//           `type` = Some(SchemaType.array),
//           items = Some(value.schema)
//           ))

//   given [T](using value: JsonSchema[T]):JsonSchema[Vector[T]] =
//     new JsonSchema[Vector[T]]:
//       def schema: FullSchema =
//         WithExtensions(SchemaInternal(
//           `type` = Some(SchemaType.array),
//           items = Some(value.schema)
//           ))

//   given [T](using value: JsonSchema[T]):JsonSchema[Seq[T]] =
//     new JsonSchema[Seq[T]]:
//       def schema: FullSchema =
//         WithExtensions(SchemaInternal(
//           `type` = Some(SchemaType.array),
//           items = Some(value.schema)
//           ))

//   given [T](using value: JsonSchema[T]):JsonSchema[Array[T]] =
//     new JsonSchema[Array[T]]:
//       def schema: FullSchema =
//         WithExtensions(SchemaInternal(
//           `type` = Some(SchemaType.array),
//           items = Some(value.schema)
//           ))

//   given [T](using value: => JsonSchema[T]):JsonSchema[Option[T]] =
//     new JsonSchema[Option[T]]:
//       def schema: FullSchema =
//         value.schema

//   given JsonSchema[Int] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.number),
//         ))

//   given JsonSchema[Long] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.number),
//         ))

//   given JsonSchema[Float] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.number),
//         ))

//   given JsonSchema[Double] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.number),
//         ))

//   given JsonSchema[BigInteger] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.number),
//         ))

//   given JsonSchema[BigInt] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.number),
//         ))

//   given JsonSchema[BigDecimal] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.number),
//         ))

//   given JsonSchema[ScalaBigDecimal] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.number),
//         ))

//   given JsonSchema[Boolean] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.boolean),
//         ))

//   given JsonSchema[String] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.string),
//         ))

//   // given JsonSchema[Array[Byte]] with
//   //   def schema: FullSchema = Schema.SchemaBytes

//   given JsonSchema[Json] with
//     def schema: FullSchema =
//       WithExtensions(SchemaInternal(
//         ))

//   inline def summonAll[T <: Tuple]: List[FullSchema] = 
//     inline erasedValue[T] match
//         case _: (t *: ts) => summonInline[JsonSchema[t]].schema :: summonAll[ts]
//         case _: EmptyTuple => Nil

//   def schemaDeref(schema: FullSchema):WithExtensions[SchemaInternal] = 
//     schema match
//       case RefTo(ref) => 
//         // println(component(ref))
//         component(ref)
//       case s:WithExtensions[SchemaInternal] => s

//   def sum(element: List[FullSchema]): FullSchema = 
//     if element.find(item => 
//       val schema = schemaDeref(item).spec
//       schema.`type` != SchemaType.string
//     ).isEmpty then
//       val es = element.map(item =>
//         val schema = schemaDeref(item).spec
//         schema.const.get
//       ).toVector
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.string),
//         `enum` = Some(es)
//         ))
//     else
//       WithExtensions(SchemaInternal(
//         oneOf = Some(element.toVector),
//         ))
  
//   def product(elements:List[FullSchema],label:String, labels:Vector[String], defaults:Map[String,Any] /*todo annotations 放这里*/): FullSchema = 
//     val itemKeys = labels
//     if itemKeys.isEmpty then
//       WithExtensions(SchemaInternal(
//         `type` = Some(SchemaType.string),
//         const = Some(Json.JString(label))
//         ))
//     else
//       val schema = SchemaInternal(
//         `type` = Some(SchemaType.`object`),
//         properties = Some(WithExtensions(labels.zip(elements).toMap))
//       )
//       WithExtensions(schema)
  

//   def tuple2List[T<: Tuple](data:T): List[String] = 
//     data match 
//       case h *: t => h.asInstanceOf[String] :: tuple2List(t)
//       case EmptyTuple => Nil

  
//   /**
//    * 如果当前component里不存在该类型的 schema， 则生成一个
//    * 返回 RefTo
//    * 
//    * SchemaItems 需要summon mirror.elementTypes 的JsonSchema
//    **/
//   given [T](using labelling:Labelling[T], defaults: DefaultValue[T], q:QualifiedName[T],m: => SchemaItems[T]): JsonSchema[T] = 
//     new JsonSchema[T]:
//       def schema = 
//         val schemaObj = 
//           val name = q.fullName
//           if !component.contains(name) then
//             // 第一时间占位， 下次进入product分支就直接返回ref了
//             component.addOne(name,null)
//             val items = m.items
//             val labels = labelling.elemLabels.toVector
//             val label = labelling.label

//             val schemas = 
//               if m.productOrSum == "product" then
//                 product(items,label,labels, defaults.defaults)
//               else
//                 sum(items)
//             schemas
//           else
//             RefTo(name)
//         schemaObj

// end JsonSchema
