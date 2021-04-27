package swopen.jsonToolbox.codec

import swopen.jsonToolbox.json.{Json,JsonNumber}
import scala.deriving.*
import shapeless3.deriving.*
import java.math.BigInteger
import scala.reflect.ClassTag

class DecodeException(val message:String) extends Exception(message)

trait Decoder[T]:
  def decode(data:Json): Either[DecodeException, T]
end Decoder


// int, long, float, double, BigInteger, BigDecimal, bool,string, option[T], List,Array,Vector, Map
object Decoder:

  def decodeError(expect: String, got: Json) = Left(DecodeException(s"expect $expect, but ${got.serialize} found"))


  def decodeSeq[T:Decoder](data:Json): Either[DecodeException,List[T]] = 
    val innerDecoder = summon[Decoder[T]]
    data match
      case Json.JArray(array) => 
        val decodedArray = array.map(innerDecoder.decode(_))
        val failed = decodedArray.find(_.isLeft)
        failed match 
          case Some(failItem) => Left(failItem.left.toOption.get)
          case None => Right(decodedArray.map(_.toOption.get).toList)
      case otherTypeValue => decodeError("Json.JArray", data)

  given [T:Decoder]: Decoder[Map[String,T]] with
    def decode(data:Json):Either[DecodeException, Map[String,T]] = 
      val innerDecoder = summon[Decoder[T]]
      data match
        case Json.JObject(map) => 
          val decodedArray = map.map{case (k,v) => (k,innerDecoder.decode(v))}
          val failed = decodedArray.find(_._2.isLeft)
          failed match 
            case Some(failItem) => Left(failItem._2.left.toOption.get)
            case None => Right(decodedArray.map{case (k,v) => (k,v.toOption.get)})
        case otherTypeValue => decodeError("Json.JObject", data)

  given Decoder[Json] with
    def decode(data:Json):Either[DecodeException, Json] = 
      Right(data)

  given [T:Decoder]: Decoder[List[T]] with
    def decode(data:Json):Either[DecodeException, List[T]] = 
      decodeSeq[T](data)

  given [T:Decoder]: Decoder[Vector[T]] with
    def decode(data:Json):Either[DecodeException, Vector[T]] = 
      decodeSeq[T](data).map(_.toVector)

  given [T:Decoder:ClassTag]: Decoder[Array[T]] with
    def decode(data:Json):Either[DecodeException, Array[T]] = 
      decodeSeq[T](data).map(_.toSeq.toArray)

  given [T:Decoder]: Decoder[Option[T]] with
    def decode(data:Json):Either[DecodeException, Option[T]] = 
      val innerDecoder = summon[Decoder[T]]
      innerDecoder.decode(data) match
        case Right(v) => Right(Some(v))
        case Left(e) => Right(None)

  given Decoder[Boolean] with
    def decode(data:Json):Either[DecodeException, Boolean] = 
      data match
        case Json.JBool(v) => Right(v)
        case otherTypeValue => decodeError("Json.JBool", data)

  given Decoder[BigInt] with
    def decode(data:Json):Either[DecodeException, BigInt] = 
      data match
        case Json.JNumber(JsonNumber.JBigInt(v)) => Right(v)
        case Json.JNumber(JsonNumber.JLong(v)) => Right(BigInt(v))
        case otherTypeValue => decodeError("JsonNumber.JBigInt", data)

  given Decoder[BigDecimal] with
    def decode(data:Json):Either[DecodeException, BigDecimal] = 
      data match
        case Json.JNumber(JsonNumber.JBigDecimal(v)) => Right(v)
        case Json.JNumber(JsonNumber.JLong(v)) => Right(BigDecimal.valueOf(v))
        case Json.JNumber(JsonNumber.JDouble(v)) => Right(BigDecimal.valueOf(v))
        case Json.JNumber(JsonNumber.JBigInt(v)) => Right(BigDecimal(v))
        case otherTypeValue => decodeError("JsonNumber.JBigDecimal", data)

  given Decoder[Float] with
    def decode(data:Json):Either[DecodeException, Float] = 
      data match
        case Json.JNumber(JsonNumber.JDouble(v)) =>
          Right(v.floatValue)
        case Json.JNumber(JsonNumber.JBigInt(v)) =>
          if v.isValidFloat then
            Right(v.floatValue)
          else
            Left(DecodeException("float value out of range: " + v.toString))
        case Json.JNumber(JsonNumber.JBigDecimal(v)) =>
            // 可能会丢失精度
            Right(v.floatValue)

        case Json.JNumber(JsonNumber.JLong(v)) => Right(v.floatValue)
        case otherTypeValue => decodeError("JsonNumber.JFloat", data)

  given Decoder[Double] with
    def decode(data:Json):Either[DecodeException, Double] = 
      data match
        case Json.JNumber(JsonNumber.JDouble(v)) => Right(v.asInstanceOf[Double])
        case Json.JNumber(JsonNumber.JBigInt(v)) =>
          if v.isValidDouble then
            Right(v.doubleValue)
          else
            Left(DecodeException("double value out of range: " + v.toString))
        case Json.JNumber(JsonNumber.JBigDecimal(v)) =>
            // 可能会丢失精度
            Right(v.doubleValue)

        case Json.JNumber(JsonNumber.JLong(v)) => Right(v.doubleValue)
        case otherTypeValue => decodeError("JsonNumber.JDouble", data)

  given Decoder[Int] with
    def decode(data:Json):Either[DecodeException, Int] = 
      data match
        case Json.JNumber(JsonNumber.JLong(v)) => 
          if v.isValidInt then
            Right(v.intValue)
          else
            Left(DecodeException(s"invalid int value: ${v}"))
        case Json.JNumber(JsonNumber.JBigInt(v)) => 
          if v.isValidInt then
            Right(v.intValue)
          else
            Left(DecodeException(s"invalid int value: ${v}"))
        case otherTypeValue => decodeError("JsonNumber.JLong", data)

  given Decoder[Long] with
    def decode(data:Json):Either[DecodeException, Long] = 
      data match
        case Json.JNumber(JsonNumber.JLong(v)) => 
            Right(v)
        case Json.JNumber(JsonNumber.JBigInt(v)) => 
          if v.isValidLong then
            Right(v.longValue)
          else
            Left(DecodeException(s"invalid long value: ${v}"))
        case otherTypeValue => decodeError("JsonNumber.JLong", data)

  given Decoder[String] with
    def decode(data:Json):Either[DecodeException, String] = 
      data match
        case Json.JString(v) => Right(v)
        case otherTypeValue => decodeError("JsonNumber.JString", otherTypeValue)

  def product[T](
    using inst: K0.ProductInstances[Decoder, T],
    labelling: Labelling[T]): Decoder[T] =
    new Decoder[T]:
      def decode(data: Json): Either[DecodeException, T] =  
        val fieldsName = labelling.elemLabels
        
        val label = labelling.label
        try
          val itemsData: Json.JObject = data match
            // 如果是字符串， 那么可能是 遇到 没有参数的 Enum了
            case Json.JString(s) => 
              if s == label then 
                Json.JObject(Map.empty) 
              else
                throw new DecodeException("label not equals enum name")
            case json:Json.JObject => json
            case _ => 
                throw new DecodeException("label not equals enum name")

          var index = 0
          val result = inst.construct([t] => (itemDecoder: Decoder[t]) => 
            val value = itemsData.value.get(fieldsName(index)).get
            val item = itemDecoder.decode(value)
            index += 1
            item match
              case Right(v) => v
              case Left(e) => throw e
          )
          Right(result)
        catch
          case e: DecodeException => Left(e)
        
  def sum[T](using inst: K0.CoproductInstances[Decoder, T], labelling: Labelling[T]): Decoder[T] = 
    new Decoder[T]:
      def decode(json: Json): Either[DecodeException, T] = 
        json match
          // 按名称序列化
          case Json.JString(s) => 
            val ordinal = labelling.elemLabels.indexOf(s)
            inst.project[Json](ordinal)(json)([t] => (s: Json, rt: Decoder[t]) => (s, rt.decode(json).toOption)) match
              case (s, None) => Left(DecodeException(s"cant decode to :${labelling.label}" + json.serialize))
              case (tl, Some(t)) => Right(t)

          case other:Json => 
            val result = labelling.elemLabels.zipWithIndex.iterator.map((p: (String, Int)) => {
              val (label, i) = p
              inst.project[Json](i)(other)([t] => (s: Json, rt: Decoder[t]) => (other,rt.decode(s).toOption)) match 
                case (s, None) => None
                case (tl, Some(t)) => Some(t)
            }).find(_.isDefined).flatten
            result match
              case Some(v) => Right(v)
              case None => Left(DecodeException("cant decode :" + labelling.label))
  

  inline given derived[T](using gen: K0.Generic[T]): Decoder[T] =
    inline gen match
      case s @ given K0.ProductGeneric[T] => 
        product[T]
      case s @ given K0.CoproductGeneric[T]  =>
        sum[T]