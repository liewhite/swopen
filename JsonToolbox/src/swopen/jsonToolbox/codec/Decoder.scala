package swopen.jsonToolbox.codec

import swopen.jsonToolbox.json.{Json,JsonNumber}
import scala.deriving.*
import scala.Product
import shapeless3.deriving.*
import shapeless3.deriving.ErasedProductInstances.ArrayProduct

class DecodeException(val message:String) extends Exception(message)

trait Decoder[T]:
  def decode(data:Json): Either[DecodeException, T]
end Decoder


object Decoder:
  given Decoder[Int] with
    def decode(data:Json):Either[DecodeException, Int] = 
      data match
        case Json.JNumber(JsonNumber.JInt(v)) => Right(v)
        case Json.JNumber(JsonNumber.JLong(v)) => 
          if v > Int.MaxValue then
            Left(DecodeException(s" too large for int: ${v}"))
          else
            Right(v.asInstanceOf[Int])
        case otherTypeValue => Left(DecodeException(s"expect JsonNumber.JInt, but ${otherTypeValue.serialize} found"))

  given Decoder[Long] with
    def decode(data:Json):Either[DecodeException, Long] = 
      data match
        case Json.JNumber(JsonNumber.JLong(v)) => Right(v)
        case otherTypeValue => Left(DecodeException(s"expect JsonNumber.JLong, but ${otherTypeValue.serialize} found"))

  def product[T](inst: K0.ProductInstances[Decoder, T], labelling: Labelling[T]): Decoder[T] =
    new Decoder[T]:
      def decode(data: Json): Either[DecodeException, T] =  
        val labels = labelling.elemLabels.toVector
        val label = labelling.label
        println((label,labels, data))
        try
          val itemsData = data match
            case Json.JString(s) => 
              if s == label then 
                Json.JObject(Map.empty) 
              else
                throw new DecodeException("label not equals enum name")
            case json:Json.JObject => json
            case _ => 
                throw new DecodeException("label not equals enum name")

          val result = itemsData match 
            case Json.JObject(obj) =>
              var index = 0
              inst.construct([t] => (itemDecoder: Decoder[t]) => 
                val value = obj.get(labels(index)).get
                val item = itemDecoder.decode(value)
                index += 1
                item match
                  case Right(v) => v
                  case Left(e) => throw e
              )
          Right(result)
        catch
          case e: DecodeException => Left(e)
        
  def sum[T](inst: K0.CoproductInstances[Decoder, T], labelling: Labelling[T]): Decoder[T] = 
    new Decoder[T]:
      def decode(json: Json): Either[DecodeException, T] = 
        json match
          // 按名称序列化
          case Json.JString(s) => 
            val ordinal = labelling.elemLabels.indexOf(s)
            println(s"index $ordinal")
            inst.project[Json](ordinal)(json)([t] => (s: Json, rt: Decoder[t]) => (s, rt.decode(json).toOption)) match
            // inst.project[Json](ordinal)(json)([t] => (s: Json, rt: Decoder[t]) => 
              // val mirror = summon[Mirror.ProductOf[t]]
              // (s, Some(mirror.fromProduct(new ArrayProduct(new Array(0)))))) match
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
  

  inline given derived[A](using gen: K0.Generic[A]): Decoder[A] =
    inline gen match
      case s:Mirror.ProductOf[A] => 
        product[A](summon[K0.ProductInstances[Decoder, A]], summon[Labelling[A]])
      case s:Mirror.SumOf[A] =>
        sum[A](summon[K0.CoproductInstances[Decoder, A]], summon[Labelling[A]])