package swopen.jsonToolbox.codec

import scala.deriving.*
import scala.jdk.CollectionConverters.*
import scala.quoted.*
import scala.util.NotGiven
import scala.compiletime.*
import java.math.BigInteger
import scala.reflect.ClassTag
import shapeless3.deriving.*

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.BigIntegerNode
import com.fasterxml.jackson.databind.node.DecimalNode
import com.fasterxml.jackson.databind.node.BooleanNode

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
              def decode(data:JsonNode) = 
                lazy val o1 = summonInline[Decoder[t1]]
                lazy val o2 = summonInline[Decoder[t2]]
                (o1.decode(data) match{
                  case Right(o) => Right(o.asInstanceOf[T])
                  case Left(_) => o2.decode(data) match {
                    case Right(o) => Right(o.asInstanceOf[T])
                    case Left(e) => Left(e)
                  }
                })
            }
            }
      case other => 
        report.error(s"not support type:,$other");???

trait CoproductDecoder extends MacroDecoder
object CoproductDecoder:
  given coproduct[T](using inst: => K0.CoproductInstances[Decoder, T], labelling: Labelling[T] ): Decoder[T] = 
    new Decoder[T]:
      def decode(json: JsonNode): Either[DecodeException, T] = 
        if json.isTextual then
          val ordinal = labelling.elemLabels.indexOf(json.asText)
          inst.project[JsonNode](ordinal)(json)([t] => (s: JsonNode, rt: Decoder[t]) => (s, rt.decode(json).toOption)) match
            case (s, None) => Left(DecodeException(s"cant decode to :${labelling.label}" + json.serialize))
            case (tl, Some(t)) => Right(t)
        else
            val result = labelling.elemLabels.zipWithIndex.iterator.map((p: (String, Int)) => {
              val (label, i) = p
              inst.project[JsonNode](i)(json)([t] => (s: JsonNode, rt: Decoder[t]) => (json,rt.decode(s).toOption)) match 
                case (s, None) => None
                case (tl, Some(t)) => Some(t)
            }).find(_.isDefined).flatten
            result match
              case Some(v) => Right(v)
              case None => Left(DecodeException("can't decode :" + labelling.label))


trait Decoder[T] extends CoproductDecoder:
  def decode(data:JsonNode): Either[DecodeException, T]
end Decoder


// int, long, float, double, BigInteger, BigDecimal, bool,string, option[T], List,Array,Vector, Map
object Decoder:

  def decodeError(expect: String, got: JsonNode) = Left(DecodeException(s"expect $expect, but ${got.toString} found"))

  inline def derived[T](using gen: K0.Generic[T]): Decoder[T] = gen.derive(product,CoproductDecoder.coproduct)

  given product[T](using inst: => K0.ProductInstances[Decoder, T],labelling: Labelling[T]): Decoder[T] =
    new Decoder[T]:
      def decode(data: JsonNode): Either[DecodeException, T] =  
        val fieldsName = labelling.elemLabels
        val label = labelling.label
        try
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
            val value = itemsData.get(fieldsName(index))
            val item = 
              if value != null then
                itemDecoder.decode(value) match
                  case Right(o) => o
                  case Left(e) => throw e
              else
                throw DecodeException(s"key not exist: ${fieldsName(index)}")
            index += 1
            item
          )
          Right(result)
        catch
          case e: DecodeException => Left(e)

  def decodeSeq[T](data:JsonNode)(using innerDecoder: Decoder[T]): Either[DecodeException,List[T]] = 
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
    def decode(data:JsonNode):Either[DecodeException, Map[String,T]] = 
      if data.isObject then
        val decodedArray = data.fields.asScala.map{item => (item.getKey,innerDecoder.decode(item.getValue))}
        val failed = decodedArray.find(_._2.isLeft)
        failed match 
          case Some(failItem) => Left(failItem._2.left.toOption.get)
          case None => Right(decodedArray.map{case (k,v) => (k,v.toOption.get)}.toMap)
      else
        decodeError("Json.JObject", data)

  given Decoder[JsonNode] with
    def decode(data:JsonNode):Either[DecodeException, JsonNode] = 
      Right(data)

  given [T:Decoder]: Decoder[List[T]] with
    def decode(data:JsonNode):Either[DecodeException, List[T]] = 
      decodeSeq[T](data)

  given [T:Decoder]: Decoder[Vector[T]] with
    def decode(data:JsonNode):Either[DecodeException, Vector[T]] = 
      decodeSeq[T](data).map(_.toVector)

  given [T:Decoder:ClassTag]: Decoder[Array[T]] with
    def decode(data:JsonNode):Either[DecodeException, Array[T]] = 
      decodeSeq[T](data).map(_.toSeq.toArray)

  given [T](using innerDecoder: Decoder[T]): Decoder[Option[T]] with
    def decode(data:JsonNode):Either[DecodeException, Option[T]] = 
      innerDecoder.decode(data) match
        case Right(v) => Right(Some(v))
        case Left(e) => Right(None)

  given Decoder[Boolean] with
    def decode(data:JsonNode):Either[DecodeException, Boolean] = 
      if data.isBoolean then
        Right(data.asBoolean)
      else
        decodeError("Json.JBool", data)

  given Decoder[BigInt] with
    def decode(data:JsonNode):Either[DecodeException, BigInt] = 
      if data.isIntegralNumber then
        Right(BigInt(data.bigIntegerValue))
      else
        decodeError("JsonNumber.JBigInt", data)

  given Decoder[BigDecimal] with
    def decode(data:JsonNode):Either[DecodeException, BigDecimal] = 
      if data.isFloatingPointNumber then
        Right(BigDecimal(data.decimalValue))
      else
        decodeError("JsonNumber.JBigDecimal", data)

  given Decoder[Float] with
    def decode(data:JsonNode):Either[DecodeException, Float] = 
      if data.isFloat then
        Right(data.floatValue)
      else
        decodeError("JsonNumber.float", data)

  given Decoder[Double] with
    def decode(data:JsonNode):Either[DecodeException, Double] = 
      if data.isDouble then
        Right(data.asDouble)
      else
        decodeError("JsonNumber.double", data)

  given Decoder[Int] with
    def decode(data:JsonNode):Either[DecodeException, Int] = 
      if data.isInt then
        Right(data.asInt)
      else
        decodeError("JsonNumber.int", data)

  given Decoder[Long] with
    def decode(data:JsonNode):Either[DecodeException, Long] = 
      if data.isLong then
        Right(data.asLong)
      else
        decodeError("JsonNumber.long", data)

  given Decoder[String] with
    def decode(data:JsonNode):Either[DecodeException, String] = 
      if data.isTextual then
        Right(data.asText)
      else
        decodeError("string", data)