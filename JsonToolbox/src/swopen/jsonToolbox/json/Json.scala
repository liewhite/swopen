package swopen.jsonToolbox.json

import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*

import java.io.{StringReader,Reader,PushbackReader}
import java.util.{ArrayList,HashMap}
import java.math.{BigDecimal,BigInteger}
import org.json.simple.parser.{JSONParser,ParseException}
import org.json.simple.*

enum JsonNumber:
  case JInt(value:Int)
  case JLong(value: Long)
  case JFloat(value: Float)
  case JDouble(value: Double)
  case JBigInt(value: BigInt)
  case JBigDecimal(value: BigDecimal)

enum Json:
  case JNull
  case JNumber(value: JsonNumber)
  case JBool(value: Boolean)
  case JString(value: String)
  case JArray(value: Vector[Json])
  case JObject(value: Map[String, Json])

  def serialize: String = 
    val s = JSONValue.toJSONString(toJavaObject)
    s

  def toJavaObject:Object = 
    this match
      case JNull => null
      case JNumber(value) => 
        value match
          case JsonNumber.JLong(value) => java.lang.Long.valueOf(value)
          case JsonNumber.JDouble(value) => java.lang.Double.valueOf(value)
          case e:JsonNumber => throw new Exception("number type:"+ e.toString)

      case JBool(value) => java.lang.Boolean.valueOf(value)
      case JString(value) => value
      case JArray(values) => 
          val list = new ArrayList[Object]()
          values.foreach(item => list.add(item.toJavaObject))
          list

      case JObject(value) => 
        val obj = new HashMap[String,Object]()
        value.foreach{
          case (k,v) => obj.put(k, v.toJavaObject)
        }
        obj


object Json:
  def deserialize(s:String): Either[ParseException,Json] = 
    val jsonParser:JSONParser = new JSONParser()
    try
      val node = jsonParser.parse(s)
      Right(nodeAsJson(node))
    catch 
      case e: ParseException =>  Left(e)
      case _ => Left(new ParseUnknownException)


  private def nodeAsJson(item: Any): Json = 
    val data = 
      if item == null then 
        Json.JNull
      else if item.isInstanceOf[String] then 
        Json.JString(item.asInstanceOf[String])
      else if item.isInstanceOf[Number] then
        // 判断数字类型
        if item.isInstanceOf[Int] then
          Json.JNumber(JsonNumber.JInt(item.asInstanceOf[Int]))
        else if item.isInstanceOf[Long] then
          Json.JNumber(JsonNumber.JLong(item.asInstanceOf[Long]))
        else if item.isInstanceOf[Float] then
          Json.JNumber(JsonNumber.JFloat(item.asInstanceOf[Float]))
        else if item.isInstanceOf[Double] then
          Json.JNumber(JsonNumber.JDouble(item.asInstanceOf[Double]))
        else if item.isInstanceOf[BigInteger] then
          Json.JNumber(JsonNumber.JBigInt(item.asInstanceOf[BigInteger]))
        else if item.isInstanceOf[BigDecimal] then
          Json.JNumber(JsonNumber.JBigDecimal(item.asInstanceOf[BigDecimal]))
        else
          ???
      else if item.isInstanceOf[Boolean] then 
        Json.JBool(item.asInstanceOf[Boolean])
      else if item.isInstanceOf[JSONObject] then 
        val map = item.asInstanceOf[JSONObject]
        Json.JObject(map.asInstanceOf[HashMap[String,Any]].asScala.map{
          case (k,v) =>
            (k,nodeAsJson(v))
        }.toMap)
      else if item.isInstanceOf[JSONArray] then
        val vec = item.asInstanceOf[JSONArray].asInstanceOf[ArrayList[Any]]
        Json.JArray(vec.asScala.map(item =>
          nodeAsJson(item)
        ).toVector)
      else
        throw new Exception("unknown json type:" + item.getClass.getName)
    data

class ParseUnknownException extends ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION)