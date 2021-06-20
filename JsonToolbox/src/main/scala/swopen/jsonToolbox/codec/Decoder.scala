package swopen.jsonToolbox.codec

import scala.deriving.*
import scala.jdk.CollectionConverters.*
import scala.quoted.*
import scala.util.NotGiven
import scala.compiletime.*
import java.math.BigInteger
import scala.reflect.ClassTag
import shapeless3.deriving.*
import swopen.jsonToolbox.schema.DefaultValue

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*

class DecodeException(val message:String) extends Exception(message)

trait MacroDecoder
object MacroDecoder:
  inline given union[T](using NotGiven[Decoder[T]]): Decoder[T] = ${ impl[T] }

  def impl[T:Type](using q: Quotes): Expr[Decoder[T]] = 
    import q.reflect._

    val repr = TypeRepr.of[T];
    repr match
      case OrType(a,b) => 
        (a.asType,b.asType) match
          case ('[t1],'[t2]) => 
            '{new Decoder[T] {
              def decode(data:JsonNode, withDefaults:Boolean = true) = 
                val o1 = summonInline[Decoder[t1]]
                val o2 = summonInline[Decoder[t2]]
                // 先不用默认值进行decode，如果失败了再用默认值试一次， 避免union type 第一个type有默认值总是成功，即使应该返回第二个type
                o1.decode(data,false) match{
                  case Right(o) => Right(o.asInstanceOf[T])
                  case Left(_) => o2.decode(data,false) match {
                    case Right(o) => Right(o.asInstanceOf[T])
                    case Left(e) => {
                      o1.decode(data) match{
                        case Right(o) => Right(o.asInstanceOf[T])
                        case Left(_) => o2.decode(data).map(_.asInstanceOf[T])
                      }
                    }
                  }
                }
            }
            }
      case other => 
        report.error(s"not support type:,$other");???

// 对于Enum 的case， SumOf和ProductOf都可以满足， 不通过继承区分优先级的话，就会出错
trait CoproductDecoder extends MacroDecoder

object CoproductDecoder:
  given coproduct[T](using inst: => K0.CoproductInstances[Decoder, T], labelling: Labelling[T] ): Decoder[T] = 
    new Decoder[T]:
      def decode(data: JsonNode, withDefaults:Boolean = true): Either[DecodeException, T] = 
        if data.isTextual then
          val ordinal = labelling.elemLabels.indexOf(data.asText)
          inst.project[JsonNode](ordinal)(data)([t] => (s: JsonNode, rt: Decoder[t]) => (s, rt.decode(data).toOption)) match
            case (s, None) => Left(DecodeException(s"cant decode to :${labelling.label}" + data.serialize))
            case (tl, Some(t)) => Right(t)
        else
            val result = labelling.elemLabels.zipWithIndex.iterator.map((p: (String, Int)) => {
              val (label, i) = p
              inst.project[JsonNode](i)(data)([t] => (s: JsonNode, rt: Decoder[t]) => (data,rt.decode(s).toOption)) match 
                case (s, None) => None
                case (tl, Some(t)) => Some(t)
            }).find(_.isDefined).flatten
            result match
              case Some(v) => Right(v)
              case None => Left(DecodeException("can't decode :" + labelling.label))

trait Decoder[T] extends CoproductDecoder:
  def decode(data:JsonNode, withDefaults:Boolean = true): Either[DecodeException, T]
end Decoder


// int, long, float, double, BigInteger, BigDecimal, bool,string, option[T], List,Array,Vector, Map
object Decoder:

  def decodeError(expect: String, got: JsonNode) = Left(DecodeException(s"expect $expect, but ${got.toString} found"))

  inline def derived[T](using gen: K0.Generic[T]): Decoder[T] = gen.derive(product,CoproductDecoder.coproduct)

  given product[T](using inst: => K0.ProductInstances[Decoder, T],labelling: Labelling[T], defaults: DefaultValue[T]): Decoder[T] =
    new Decoder[T]:
      def decode(data:JsonNode, withDefaults:Boolean = true): Either[DecodeException, T] =  
        def decodePhase[T](inst: K0.ProductInstances[Decoder,T], data:JsonNode,labelling:Labelling[T],defaultValues: Map[String, JsonNode]) = 
          val label = labelling.label
          val fieldsName = labelling.elemLabels
          val itemsData: ObjectNode = 
            if data.isTextual then
              val stringValue = data.asText
              // 如果是字符串， 那么可能是 遇到 没有参数的 Enum了
              if stringValue == label then 
                ObjectNode(JsonNodeFactory.instance, Map.empty[String,JsonNode].asJava)
              else
                throw new DecodeException("label not equals enum name")
            else if data.isObject then
              ObjectNode(JsonNodeFactory.instance,data.fields.asScala.map(item => (item.getKey,item.getValue)).toMap.asJava)
            else
              throw new DecodeException( s"expect product, got: ${data.toString}")

          var index = 0
          val result = inst.construct([t] => (itemDecoder: Decoder[t]) => 
            // 处理默认值
            val value = itemsData.get(fieldsName(index)) match 
              case n if n == null =>
                if defaultValues.contains(fieldsName(index)) then
                  defaultValues(fieldsName(index))
                else
                  throw DecodeException(s"key not exist: ${fieldsName(index)}")
              case v => v
            val item = itemDecoder.decode(value) match
              case Right(o) => o
              case Left(e) => throw e

            index += 1
            item
          )
          Right(result)
        // 先不用默认值处理一边，如果没有结果，那么加上默认值再处理一遍
        try
          decodePhase[T](inst, data, labelling, if withDefaults then defaults.defaults else Map.empty[String, JsonNode])
        catch
          // case e: DecodeException => 
          //   try
          //     val result = decodePhase[T](inst, data, labelling, defaults.defaults)
          //     result
          //   catch
          case e: DecodeException => Left(e)

  def decodeSeq[T](data:JsonNode, withDefaults:Boolean = true)(using innerDecoder: Decoder[T]): Either[DecodeException,List[T]] = 
    if data.isArray then
      val array = data
      val decodedArray = array.elements.asScala.map(innerDecoder.decode(_))
      val failed = decodedArray.find(_.isLeft)
      failed match 
        case Some(failItem) => Left(failItem.left.toOption.get)
        case None => Right(decodedArray.map(_.toOption.get).toList)
    else
      decodeError("Json.JArray", data)

  given [T](using innerDecoder: Decoder[T]): Decoder[Map[String,T]] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Map[String,T]] = 
      if data.isObject then
        val decodedArray = data.fields.asScala.map{item => (item.getKey,innerDecoder.decode(item.getValue))}
        val failed = decodedArray.find(_._2.isLeft)
        failed match 
          case Some(failItem) => Left(failItem._2.left.toOption.get)
          case None => Right(decodedArray.map{case (k,v) => (k,v.toOption.get)}.toMap)
      else
        decodeError("Json.JObject", data)

  given Decoder[JsonNode] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, JsonNode] = 
      Right(data)

  given [T:Decoder]: Decoder[List[T]] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, List[T]] = 
      decodeSeq[T](data)

  given [T:Decoder]: Decoder[Vector[T]] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Vector[T]] = 
      decodeSeq[T](data).map(_.toVector)

  given [T:Decoder:ClassTag]: Decoder[Array[T]] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Array[T]] = 
      decodeSeq[T](data).map(_.toSeq.toArray)

  given [T](using innerDecoder: Decoder[T]): Decoder[Option[T]] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Option[T]] = 
      innerDecoder.decode(data) match
        case Right(v) => Right(Some(v))
        case Left(e) => Right(None)

  given Decoder[Boolean] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Boolean] = 
      if data.isBoolean then
        Right(data.asBoolean)
      else
        decodeError("Json.JBool", data)

  given Decoder[BigInt] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, BigInt] = 
      if data.isIntegralNumber then
        Right(BigInt(data.bigIntegerValue))
      else
        decodeError("JsonNumber.JBigInt", data)

  given Decoder[BigDecimal] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, BigDecimal] = 
      if data.isFloatingPointNumber then
        Right(BigDecimal(data.decimalValue))
      else
        decodeError("JsonNumber.JBigDecimal", data)

  given Decoder[Float] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Float] = 
      if data.isFloat then
        Right(data.floatValue)
      else
        decodeError("JsonNumber.float", data)

  given Decoder[Double] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Double] = 
      if data.isDouble then
        Right(data.asDouble)
      else
        decodeError("JsonNumber.double", data)

  given Decoder[Int] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Int] = 
      if data.isInt then
        Right(data.asInt)
      else
        decodeError("JsonNumber.int", data)

  given Decoder[Long] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, Long] = 
      if data.isLong then
        Right(data.asLong)
      else
        decodeError("JsonNumber.long", data)

  given Decoder[String] with
    def decode(data:JsonNode, withDefaults:Boolean = true):Either[DecodeException, String] = 
      if data.isTextual then
        Right(data.asText)
      else
        decodeError("string", data)