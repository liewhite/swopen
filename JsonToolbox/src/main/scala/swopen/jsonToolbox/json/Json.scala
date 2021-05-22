package swopen.jsonToolbox.json

import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*

import java.io.{StringReader,Reader,PushbackReader}
import java.util.{ArrayList,HashMap}
import java.math.{BigDecimal,BigInteger}
import scala.math.{BigDecimal as ScalaBD,BigInt}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*

enum JsonNumber:
  // case JInt(value:Int) // 内部表示不再使用int, decode的时候需要int再生成
  case JLong(value: Long)
  // case JFloat(value: Float)
  case JDouble(value: Double)
  case JBigInt(value: BigInt)
  case JBigDecimal(value: ScalaBD)

enum Json:
  case JNull
  case JNumber(value: JsonNumber)
  case JBool(value: Boolean)
  case JString(value: String)
  case JArray(value: Seq[Json])
  case JObject(value: Map[String, Json])

  def serialize: String = 
    val mapper = ObjectMapper()
    val s = mapper.writeValueAsString(this.toJacksonTree)
    s

  def toJacksonTree:JsonNode = 
    this match
      case JNull => NullNode.getInstance
      case JNumber(value) => 
        value match
          case JsonNumber.JLong(value) => LongNode(value)
          case JsonNumber.JDouble(value) => DoubleNode(value)
          case JsonNumber.JBigInt(value) => BigIntegerNode(value.bigInteger)
          case JsonNumber.JBigDecimal(value) => DecimalNode(value.bigDecimal)

      case JBool(value) => BooleanNode.valueOf(value)
      case JString(value) => TextNode(value)
      case JArray(values) => 
          val list = new ArrayNode(JsonNodeFactory.instance)
          values.foreach(item => list.add(item.toJacksonTree))
          list

      case JObject(value) => 
        val obj = new ObjectNode(JsonNodeFactory.instance)
        // 使用case匹配貌似有bug,会多匹配一次
        value.foreach(item => obj.set(item._1,item._2.toJacksonTree))
        //   case (k,v) => obj.set(k, v.toJacksonTree)
        //   case err => println(s"this: $this,     err: $err")
        // }
        obj


object Json:
  def deserialize(s:String): Either[ParseException,Json] = 
    val mapper:ObjectMapper = new ObjectMapper()
    try
      val node = mapper.readTree(s)
      Right(fromJacksonTree(node))
    catch 
      case e: ParseException =>  Left(e)
      case e => Left(ParseException("unknown parse exception"))


  private def fromJacksonTree(item: JsonNode): Json = 
    val data = 
      if item.getNodeType == JsonNodeType.NULL then 
        Json.JNull
      else if item.getNodeType == JsonNodeType.STRING then 
        Json.JString(item.textValue)
      // 判断数字类型
      else if item.isInt then
        Json.JNumber(JsonNumber.JLong(item.longValue))

      else if item.isLong then
        Json.JNumber(JsonNumber.JLong(item.longValue))

      else if item.isFloat then
        Json.JNumber(JsonNumber.JDouble(item.floatValue))

      else if item.isDouble then
        Json.JNumber(JsonNumber.JDouble(item.doubleValue))

      else if item.isBigInteger then
        Json.JNumber(JsonNumber.JBigInt(item.bigIntegerValue))

      else if item.isBigDecimal then
        Json.JNumber(JsonNumber.JBigDecimal(item.decimalValue))

      else if item.isBoolean then 
        Json.JBool(item.booleanValue)

      else if item.isObject then 
        val map = item.fields.asScala.map((entry) => (entry.getKey, fromJacksonTree(entry.getValue))).toMap
        Json.JObject(map)

      else if item.isArray then
        val arr = item.elements.asScala.map(fromJacksonTree(_)).toVector
        Json.JArray(arr)
      else
        throw new Exception("unknown json type:" + item.getClass.getName)
    data

class ParseException(msg:String) extends Exception(msg)