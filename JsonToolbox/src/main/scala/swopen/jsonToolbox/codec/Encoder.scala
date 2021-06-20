package swopen.jsonToolbox.codec

import scala.math.{BigDecimal, BigInt}
import scala.jdk.CollectionConverters.*
import scala.deriving.*
import scala.quoted.*
import scala.compiletime.*
import scala.util.NotGiven

import shapeless3.deriving.{K0, Continue, Labelling}
import swopen.jsonToolbox.JsonBehavior.*
import swopen.jsonToolbox.typeclasses.{
  RepeatableAnnotation,
  RepeatableAnnotations
}
import swopen.jsonToolbox.utils.SummonUtils
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*

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
object CoproductEncoder:
  given coproduct[T](using
      inst: => K0.CoproductInstances[Encoder, T]
  ): Encoder[T] =
    new Encoder[T]:
      def encode(t: T): JsonNode =
        inst.fold(t)([t] => (st: Encoder[t], t: t) => st.encode(t))

trait Encoder[T] extends CoproductEncoder:
  def encode(t: T): JsonNode
end Encoder

object Encoder:
  inline def derived[A](using gen: K0.Generic[A]): Encoder[A] =
    gen.derive(product, CoproductEncoder.coproduct)

  given product[T](using
      inst: => K0.ProductInstances[Encoder, T],
      labelling: Labelling[T]
  ): Encoder[T] =
    new Encoder[T]:
      def encode(t: T): JsonNode =
        val fieldsName = labelling.elemLabels

        if (fieldsName.isEmpty) then TextNode(labelling.label)
        else
          val elems: List[JsonNode] = inst.foldLeft(t)(List.empty[JsonNode])(
            [t] =>
              (acc: List[JsonNode], st: Encoder[t], t: t) =>
                Continue(st.encode(t) :: acc)
          )
          val rawMap = fieldsName.zip(elems.reverse).toMap
          ObjectNode(JsonNodeFactory.instance, rawMap.asJava)

  /** map encoder
    */
  given [T](using encoder: Encoder[T]): Encoder[Map[String, T]] with
    def encode(t: Map[String, T]): JsonNode =
      ObjectNode(
        JsonNodeFactory.instance,
        t.map { case (k, v) => (k, encoder.encode(v)) }.asJava
      )

  /** seq encoder
    */
  given [T](using encoder: Encoder[T]): Encoder[Vector[T]] with
    def encode(t: Vector[T]) =
      ArrayNode(JsonNodeFactory.instance, t.map(encoder.encode(_)).asJava)

  given [T](using encoder: Encoder[T]): Encoder[List[T]] with
    def encode(t: List[T]) =
      ArrayNode(JsonNodeFactory.instance, t.map(encoder.encode(_)).asJava)

  given [T](using encoder: Encoder[T]): Encoder[Array[T]] with
    def encode(t: Array[T]) =
      ArrayNode(
        JsonNodeFactory.instance,
        Vector.from(t).map(encoder.encode(_)).asJava
      )

  /** option encoder
    */
  given [T](using e: Encoder[T]): Encoder[Option[T]] with
    def encode(t: Option[T]) =
      t match
        case Some(v) =>
          e.encode(v)
        case None => NullNode.instance

  /** number encoder
    */
  given Encoder[Float] with
    def encode(t: Float) = FloatNode(t)

  given Encoder[Double] with
    def encode(t: Double) = DoubleNode(t)

  given Encoder[Int] with
    def encode(t: Int) = IntNode(t)

  given Encoder[Long] with
    def encode(t: Long) = LongNode(t)

  given Encoder[BigInt] with
    def encode(t: BigInt) = BigIntegerNode(t.bigInteger)

  given Encoder[BigDecimal] with
    def encode(t: BigDecimal) = DecimalNode(t.bigDecimal)

  given Encoder[String] with
    def encode(t: String) = TextNode(t)

  given Encoder[Boolean] with
    def encode(t: Boolean) = BooleanNode.valueOf(t)

  given Encoder[Null] with
    def encode(t: Null) = NullNode.instance

  given Encoder[JsonNode] with
    def encode(t: JsonNode) = t

end Encoder
