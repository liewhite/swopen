package io.github.liewhite.sql

import scala.compiletime.*
import scala.quoted.*
import scala.deriving.Mirror

import shapeless3.deriving.{K0, Continue, Labelling}
import io.github.liewhite.common.SummonUtils.summonAll


trait Table[T]{
  def tableName: String
  // 这里的Column类型和selectable返回的类型要一致
  def columns: Map[String,Column]
}

object Table{
  inline given derived[A](using gen: Mirror.ProductOf[A],labelling: Labelling[A]): Table[A] =
    val columnTypes = summonAll[ColumnLike, gen.MirroredElemTypes]
    val tableName = labelling.label
    val cols = labelling.elemLabels.zip(columnTypes).map{
      case (label, tp) => Column(tableName, label, tp.columnType)
    }
    // todo 获取各种annotations
    new Table{
      def tableName = labelling.label
      def columns = labelling.elemLabels.zip(cols).toMap
    }

}

class Props[T](using table:Table[T]) extends Selectable:
  def selectDynamic(name: String): Any ={
    table.columns(name)
  }

transparent inline def from[T] =
  ${ fromImpl[T] }


private def fromImpl[T: Type](using Quotes): Expr[Any] =
  import quotes.reflect.*

  def recur[mels : Type, mets:Type](baseType: TypeRepr): TypeRepr = {
      Type.of[mels] match
        case '[mel *: melTail] => {
          Type.of[mets] match {
            case '[head *: tail] => {
              val label = Type.valueOfConstant[mel].get.toString
              Expr.summon[ColumnLike[head]] match {
                case Some('{ $m: ColumnLike[head] {type Underlying = u}}) => {
                  recur[melTail, tail](Refinement(baseType, label, TypeRepr.of[Column{type Underlying = u}]))
                }
                case None => {
                  report.error("Field implementation not found:")
                  ???
                }
                case _ => {
                  report.error("Unknown error when summon Field[head]")
                  ???
                }
              }
            }
          }
        }
        case '[EmptyTuple] => baseType
  }

  Expr.summon[Mirror.ProductOf[T]].get match {
    case '{ $m: Mirror.ProductOf[T] {type MirroredElemLabels = mels; type MirroredElemTypes = mets } } =>
      recur[mels,mets](TypeRepr.of[Props]).asType match {
            case '[tpe] =>
              '{
                val table = summonInline[Table[T]]
                val p = Props[T](using table)
                p.asInstanceOf[tpe]
              }
      }
  }