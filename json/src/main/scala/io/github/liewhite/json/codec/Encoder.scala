package io.github.liewhite.json.codec

import scala.math.{BigDecimal, BigInt}
import scala.jdk.CollectionConverters.*
import scala.deriving.*
import scala.quoted.*
import scala.compiletime.*
import scala.util.NotGiven

import shapeless3.deriving.{K0, Continue, Labelling}

import io.circe.Json

import io.github.liewhite.json.JsonBehavior.*
import io.github.liewhite.json.typeclass.*
import io.github.liewhite.json.annotations.*
import io.github.liewhite.json.utils.SummonUtils
import io.github.liewhite.json.error.JsonError
import io.github.liewhite.json.error.JsonErrorType

trait UnionEncoder
object UnionEncoder:
  inline given union[T]: Encoder[T] = ${ impl[T] }

  def impl[T: Type](using q: Quotes): Expr[Encoder[T]] =
    import q.reflect._

    val repr = TypeRepr.of[T];
    repr match
      case OrType(a, b) =>
        (a.asType, b.asType) match
          case ('[t1], '[t2]) =>
            '{
              new Encoder[T] {
                def encode(t: T) =
                  val o1 = summonInline[Encoder[t1]]
                  val o2 = summonInline[Encoder[t2]]
                  t match
                    case o: t1 => o1.encode(o)
                    case o: t2 => o2.encode(o)
              }
            }
      case other =>
        report.error(s"not support type:,$other"); ???

trait CoproductEncoder extends UnionEncoder
object CoproductEncoder{
  given coproduct[T](using
      inst: => K0.CoproductInstances[Encoder, T],
      labelling: Labelling[T],
  ): Encoder[T] =
    new Encoder[T]:
      def encode(t: T): Json =
        inst.fold(t)([t] => (st: Encoder[t], t: t) => {
          // 对于enum case object， 会先匹配到
          st.encode(t)
        })
}

trait Encoder[T] extends CoproductEncoder:
  def encode(t: T): Json
end Encoder

object Encoder:
  // inline given derived[A](using gen: K0.Generic[A]): Encoder[A] =
  //   gen.derive(product, CoproductEncoder.coproduct)
  inline given product[T](using
      inst: => K0.ProductInstances[Encoder, T],
      labelling: Labelling[T],
      flats: RepeatableAnnotations[Flat,T],
  ): Encoder[T] =
    new Encoder[T]:
      def encode(t: T): Json =
        val fieldsName = labelling.elemLabels

        // 没有成员的product， 按照singleton处理
        if (fieldsName.isEmpty) then Json.fromString(labelling.label)
        else
          val elems: List[Json] = inst.foldLeft(t)(List.empty[Json])(
            [t] =>
              (acc: List[Json], st: Encoder[t], t: t) =>
                Continue(st.encode(t) :: acc)
          )
          val flatFlags = flats()
          // name, value, flatFlag
          val columns = fieldsName.zip(elems.reverse).zip(flatFlags).map(item => (item._1._1,item._1._2,item._2))
          val flattenColoums = columns.flatMap(item => {
            if(item._2.isString) {
              Vector((item._1,item._2))
            }
            else if(item._3.isEmpty){
              Vector((item._1,item._2))
            }else{
              if(!item._2.isObject){
                throw new JsonError(JsonErrorType.EncodeError, "flat need object,but got:" + item._2)
              }
              item._2.asObject.get.toMap.map(item => (item._1,item._2)).toVector
            }
          }).toMap
          Json.fromFields(flattenColoums)

  /** map encoder
    */
  given [T](using encoder: Encoder[T]): Encoder[Map[String, T]] with
    def encode(t: Map[String, T]): Json =
      Json.fromFields(
        t.map { case (k, v) => (k, encoder.encode(v)) }
      )

  /** seq encoder
    */
  given [T](using encoder: Encoder[T]): Encoder[Vector[T]] with
    def encode(t: Vector[T]) =
      Json.fromValues(t.map(encoder.encode(_)))

  given [T](using encoder: Encoder[T]): Encoder[List[T]] with
    def encode(t: List[T]) =
      Json.fromValues(t.map(encoder.encode(_)))

  given [T](using encoder: Encoder[T]): Encoder[Array[T]] with
    def encode(t: Array[T]) =
      Json.fromValues(t.map(encoder.encode(_)))

  /** option encoder
    */
  given [T](using e: Encoder[T]): Encoder[Option[T]] with
    def encode(t: Option[T]) =
      t match
        case Some(v) =>
          e.encode(v)
        case None => Json.Null

  given Encoder[EmptyTuple] with
    def encode(t: EmptyTuple) = Json.fromValues(List.empty)

  given [H: Encoder, T <:Tuple:Encoder](using headEncoder: Encoder[H], tailEncoder: Encoder[T]): Encoder[H *: T] with
    def encode(t: H*: T) = Json.fromValues(headEncoder.encode(t.head).asArray.get.appended(tailEncoder.encode(t.tail)))
  /** number encoder
    */
  given Encoder[Float] with
    def encode(t: Float) = Json.fromFloat(t).get

  given Encoder[Double] with
    def encode(t: Double) = Json.fromDouble(t).get

  given Encoder[Int] with
    def encode(t: Int) = Json.fromInt(t)

  given Encoder[Long] with
    def encode(t: Long) = Json.fromLong(t)

  given Encoder[BigInt] with
    def encode(t: BigInt) = Json.fromBigInt(t.bigInteger)

  given Encoder[BigDecimal] with
    def encode(t: BigDecimal) = Json.fromBigDecimal(t.bigDecimal)

  given Encoder[String] with
    def encode(t: String) = Json.fromString(t)

  given Encoder[Boolean] with
    def encode(t: Boolean) = Json.fromBoolean(t)

  given Encoder[Null] with
    def encode(t: Null) = Json.Null

  given Encoder[Json] with
    def encode(t: Json) = t

end Encoder