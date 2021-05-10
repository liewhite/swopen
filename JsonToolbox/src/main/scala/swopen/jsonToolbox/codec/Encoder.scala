package swopen.jsonToolbox.codec

import scala.math.{BigDecimal,BigInt}
import scala.deriving.*
import scala.quoted.*
import scala.compiletime.*
import shapeless3.deriving.*

import swopen.jsonToolbox.json.{Json,JsonNumber}
import swopen.jsonToolbox.utils.SummonUtils


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

  given Encoder[Json] with
    def encode(t:Json) = t

  def product[T](
    using inst: K0.ProductInstances[Encoder, T],
    labelling: Labelling[T],
    ): Encoder[T] =   
    new Encoder[T]:
      def encode(t: T): Json = 
        val fieldsName = labelling.elemLabels

        if(fieldsName.isEmpty) then
          Json.JString(labelling.label)
        else 
          val elems: List[Json] = inst.foldLeft(t)(List.empty[Json])(
            [t] => (acc: List[Json], st: Encoder[t], t: t) => Continue(st.encode(t) :: acc)
          )
          Json.JObject(fieldsName.zip(elems.reverse).toMap)

  def sum[T](using inst:  K0.CoproductInstances[Encoder, T]): Encoder[T]  =
    new Encoder[T]:
      def encode(t: T): Json = inst.fold(t)([t] => (st: Encoder[t], t: t) => st.encode(t))

    
  import shapeless3.deriving.Annotation
  import swopen.jsonToolbox.modifier.Modifier
  import swopen.jsonToolbox.utils.OptionGiven

  inline given derived[T](using gen: K0.Generic[T]): Encoder[T] =
    // val modifier = scala.Predef.summon[OptionGiven[Annotation[Modifier,T]]]
    // println(modifier)
    // 支持递归数据结构， 要先支持schema引用
    inline gen match
      case s @ given K0.ProductGeneric[T] => 
        product[T]
      case s @ given K0.CoproductGeneric[T]  =>
        sum[T]

  // // for union type 
  inline given [T]: Encoder[T] = ${ impl[T] }

  def impl[T:Type](using q: Quotes): Expr[Encoder[T]] = 
    import q.reflect._

    val repr = TypeRepr.of[T];
    repr match
      case OrType(a,b) => 
        (a.asType,b.asType) match
          case ('[t1],'[t2]) => 
            '{new Encoder[T] {
              def encode(data:T) = 
                val o1 = summonInline[Encoder[t1]]
                val o2 = summonInline[Encoder[t2]]
                data match
                  case o:t1 => o1.encode(o)
                  case o:t2 => o2.encode(o)
            }
            }
      case other => 
        other.asType match
          case '[t] =>
            '{new Encoder[T] {
              def encode(data:T) = 
                val o1 = summonInline[Encoder[t]].asInstanceOf[Encoder[T]]
                o1.encode(data)
            }
            }

end Encoder