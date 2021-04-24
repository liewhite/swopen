package swopen.jsonToolbox.codec

import scala.compiletime.erasedValue
import scala.deriving.Mirror
import shapeless3.deriving.*

import swopen.jsonToolbox.json.{Json,JsonNumber}


trait Encoder[T]:
  def encode(t:T):Json


end Encoder


object Encoder:
  given Encoder[Int] with
    def encode(t:Int) = Json.JNumber(JsonNumber.JLong(t))

  given Encoder[Long] with
    def encode(t:Long) = Json.JNumber(JsonNumber.JLong(t))

  given Encoder[String] with
    def encode(t:String) = Json.JString(t)

  given Encoder[Boolean] with
    def encode(t:Boolean) = Json.JBool(t)

  given Encoder[Null] with
    def encode(t:Null) = Json.JNull

  def product[T](inst: K0.ProductInstances[Encoder, T], labelling: Labelling[T]): Encoder[T] =   
    new Encoder[T]:
      def encode(t: T): Json = 
        // TODO 处理modifier
        if(labelling.elemLabels.isEmpty) then 
          Json.JString(labelling.label)
        // if(labelling.elemLabels.isEmpty) Json.JObject(Map.empty)
        else 
          val elems: List[Json] = inst.foldLeft(t)(List.empty[Json])(
            [t] => (acc: List[Json], st: Encoder[t], t: t) => Continue(st.encode(t) :: acc)
          )
          Json.JObject(labelling.elemLabels.zip(elems.reverse).toMap)

  def sum[T](inst: K0.CoproductInstances[Encoder, T]): Encoder[T]  =
    new Encoder[T]:
      def encode(t: T): Json = inst.fold(t)([t] => (st: Encoder[t], t: t) => st.encode(t))
  

  inline given derived[A](using gen: K0.Generic[A]): Encoder[A] =
    inline gen match
      case s:Mirror.ProductOf[A] => product[A](summon[K0.ProductInstances[Encoder, A]], summon[Labelling[A]])
      case s:Mirror.SumOf[A] => sum[A](summon[K0.CoproductInstances[Encoder, A]])

end Encoder