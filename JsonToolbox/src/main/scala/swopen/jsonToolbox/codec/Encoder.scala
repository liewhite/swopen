package swopen.jsonToolbox.codec

import scala.math.{BigDecimal,BigInt}
import scala.deriving.*
import scala.quoted.*
import scala.compiletime.*
import scala.util.NotGiven

import shapeless3.deriving.{K0,Continue,Labelling}

import swopen.jsonToolbox.json.{Json,JsonNumber}
import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.typeclasses.{RepeatableAnnotation,RepeatableAnnotations}
import swopen.jsonToolbox.utils.SummonUtils

trait UnionEncoder
object UnionEncoder:
  // inline given union[T](using NotGiven[Encoder[T]]): Encoder[T] = ${ impl[T] }
  inline given union[T]: Encoder[T] = ${ impl[T] }

  def impl[T:Type](using q: Quotes): Expr[Encoder[T]] = 
    import q.reflect._

    val repr = TypeRepr.of[T];
    repr match
      case OrType(a,b) => 
        (a.asType,b.asType) match
          case ('[t1],'[t2]) => 
            '{new Encoder[T] {
              def encode(t:T) = 
                lazy val o1 = summonInline[Encoder[t1]]
                lazy val o2 = summonInline[Encoder[t2]]
                t match
                  case o:t1 => o1.encode(o)
                  case o:t2 => o2.encode(o)
                  // case o:t1 => ${Expr.summon[Encoder[t1]].get}.encode(o)
                  // case o:t2 => ${Expr.summon[Encoder[t2]].get}.encode(o)
            }}
      case other => 
        report.error(s"not support type:,$other");???

trait CoproductEncoder extends UnionEncoder
object CoproductEncoder:
  given splsCoproduct[T](using inst: => K0.CoproductInstances[Encoder, T]): Encoder[T]  =
    new Encoder[T]:
      def encode(t: T): Json = inst.fold(t)([t] => (st: Encoder[t], t: t) => st.encode(t))

trait Encoder[T] extends CoproductEncoder:
  def encode(t: T):Json
  
end Encoder


object Encoder:
  inline def derived[A](using gen: K0.Generic[A]): Encoder[A] = gen.derive(splsProduct,CoproductEncoder.splsCoproduct)

  given splsProduct[T](using inst: => K0.ProductInstances[Encoder, T],labelling: Labelling[T], annotation: RepeatableAnnotation[IgnoreNull,T]): Encoder[T] =  
    new Encoder[T]:
      def encode(t: T): Json = 
        val fieldsName = labelling.elemLabels

        if(fieldsName.isEmpty) then
          Json.JString(labelling.label)
        else 
          val elems: List[Json] = inst.foldLeft(t)(List.empty[Json])(
            [t] => (acc: List[Json], st: Encoder[t], t: t) => Continue(st.encode(t) :: acc)
          )
          // val rawJson = Json.JObject(fieldsName.zip(elems.reverse).toMap)
          val rawMap = fieldsName.zip(elems.reverse).toMap
          Json.JObject(
            if annotation().nonEmpty then
              rawMap.filter(_._2 != Json.JNull)
            else rawMap
          ) 

  /**
   *  map encoder
   */
  given [T](using encoder: Encoder[T]): Encoder[Map[String,T]] with
    def encode(t:Map[String,T]) = 
      Json.JObject(t.map{case (k,v) => (k, encoder.encode(v))})

  /**
   *  seq encoder
   */
  given [T](using encoder: Encoder[T]): Encoder[Vector[T]] with
    def encode(t:Vector[T]) = 
      Json.JArray(t.map(encoder.encode(_)))

  given [T](using encoder: Encoder[T]): Encoder[List[T]] with
    def encode(t:List[T]) = 
      Json.JArray(t.map(encoder.encode(_)))

  given [T](using encoder: Encoder[T]): Encoder[Array[T]] with
    def encode(t:Array[T]) = 
      Json.JArray(Vector.from(t).map(encoder.encode(_)))

  /**
   *  option encoder
   */
  given [T](using e: Encoder[T]): Encoder[Option[T]] with
    def encode(t:Option[T]) = 
      t match
        case Some(v) => 
          e.encode(v)
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

end Encoder