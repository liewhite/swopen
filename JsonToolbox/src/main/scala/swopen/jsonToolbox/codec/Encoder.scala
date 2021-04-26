package swopen.jsonToolbox.codec

import scala.math.{BigDecimal,BigInt}
import scala.deriving.*
import shapeless3.deriving.*

import swopen.jsonToolbox.json.{Json,JsonNumber}
import swopen.jsonToolbox.modifier.Modifier
import swopen.jsonToolbox.utils.OptionGiven


trait Encoder[T]:
  def encode(t: T):Json
end Encoder


object Encoder:
  /**
   *  map encoder
   */
  given [T:Encoder]: Encoder[Map[String,T]] with
    def encode(t:Map[String,T]) = 
      val encoder = summon[Encoder[T]]
      Json.JObject(t.map{case (k,v) => (k, encoder.encode(v))})

  /**
   *  seq encoder
   */
  given [T:Encoder]: Encoder[Vector[T]] with
    def encode(t:Vector[T]) = 
      val encoder = summon[Encoder[T]]
      Json.JArray(t.map(encoder.encode(_)))

  given [T:Encoder]: Encoder[List[T]] with
    def encode(t:List[T]) = 
      val encoder = summon[Encoder[T]]
      Json.JArray(t.map(encoder.encode(_)))

  given [T:Encoder]: Encoder[Array[T]] with
    def encode(t:Array[T]) = 
      val encoder = summon[Encoder[T]]
      Json.JArray(Vector.from(t).map(encoder.encode(_)))

  /**
   *  option encoder
   */
  given [T:Encoder]: Encoder[Option[T]] with
    def encode(t:Option[T]) = 
      t match
        case Some(v) => summon[Encoder[T]].encode(v)
        case None => Json.JNull

  /**
   *  number encoder
   */
  given Encoder[Float] with
    def encode(t:Float) = Json.JNumber(JsonNumber.JDouble(t))

  given Encoder[Double] with
    def encode(t:Double) = Json.JNumber(JsonNumber.JDouble(t))

  given Encoder[Int] with
    def encode(t:Int) = Json.JNumber(JsonNumber.JLong(t))

  given Encoder[Long] with
    def encode(t:Long) = Json.JNumber(JsonNumber.JLong(t))

  given Encoder[BigInt] with
    def encode(t:BigInt) = Json.JNumber(JsonNumber.JBigInt(t))

  given Encoder[BigDecimal] with
    def encode(t:BigDecimal) = Json.JNumber(JsonNumber.JBigDecimal(t))

  given Encoder[String] with
    def encode(t:String) = Json.JString(t)

  given Encoder[Boolean] with
    def encode(t:Boolean) = Json.JBool(t)

  given Encoder[Null] with
    def encode(t:Null) = Json.JNull

  def product[T](
    inst: K0.ProductInstances[Encoder, T],
    labelling: Labelling[T], 
    modifier: Annotations[Modifier,T],
    productModifer: Option[Annotation[Modifier,T]],
    ): Encoder[T] =   
    new Encoder[T]:
      def encode(t: T): Json = 
        val modifers: List[Option[Modifier]] = modifier.apply().toList.asInstanceOf
        
        val fieldsName = modifers.zip(labelling.elemLabels).map{
          case(m,l) => m match 
            case Some(mod) => mod.rename
            case None => l
        }
        if(fieldsName.isEmpty) then
          val name = productModifer match
            case Some(m) => m.apply().rename
            case None => labelling.label
          Json.JString(name)
        else 
          val elems: List[Json] = inst.foldLeft(t)(List.empty[Json])(
            [t] => (acc: List[Json], st: Encoder[t], t: t) => Continue(st.encode(t) :: acc)
          )
          Json.JObject(fieldsName.zip(elems.reverse).toMap)

  def sum[T](inst: K0.CoproductInstances[Encoder, T]): Encoder[T]  =
    new Encoder[T]:
      def encode(t: T): Json = inst.fold(t)([t] => (st: Encoder[t], t: t) => st.encode(t))
  
  // inline 会在展开处进行 using的寻找
  // mirror和shapeless不能混用
  inline given derived[T](using gen: K0.Generic[T]): Encoder[T] =
  // inline given derived[T](using gen: Mirror.Of[T]): Encoder[T] =
    inline gen match
      // case s: Mirror.ProductOf[T] => 
      case s @ given K0.ProductGeneric[T] => 
        val modifier = summon[OptionGiven[Annotation[Modifier,T]]]
        product(
          summon[K0.ProductInstances[Encoder, T]], 
          summon[Labelling[T]],
          summon[Annotations[Modifier,T]],
          // None
          modifier.give
          )
      case s @ given K0.CoproductGeneric[T]  =>
        // val inst:K0.CoproductInstances[Encoder,T] = ErasedCoproductInstances[K0.type, Encoder[T], K0.LiftP[Encoder, s.MirroredElemTypes]](s)
        sum[T](summon[K0.CoproductInstances[Encoder, T]])
        // sum[T](inst)
end Encoder