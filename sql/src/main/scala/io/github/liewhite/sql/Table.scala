package io.github.liewhite.sql

import scala.compiletime.*
import scala.quoted.*
import scala.deriving.Mirror

import shapeless3.deriving.{K0, Continue, Labelling}
import io.github.liewhite.common.SummonUtils.summonAll

trait Table[T] extends Selectable{
  def tableName: String
  // 这里的Column类型和selectable返回的类型要一致
  def columns: Map[String,DBField]

  def selectDynamic(name: String): Any ={
    columns(name)
  }

}

object Table{
  inline given derived[A](using gen: Mirror.ProductOf[A],labelling: Labelling[A]): Table[A] =
    val columnTypes = summonAll[DBFieldLike, gen.MirroredElemTypes]
    val tableName = labelling.label
    val cols = labelling.elemLabels.zip(columnTypes).map{
      case (label, tp) => tp.toField(label, tableName)
    }
    // todo 获取各种annotations
    new Table{
      def tableName = labelling.label
      def columns = labelling.elemLabels.zip(cols).toMap
    }

  transparent inline def apply[T] ={
    ${ queryImpl[T] }
  }

  private def queryImpl[T: Type](using Quotes): Expr[Any] = {
    import quotes.reflect.*

    def recur[mels : Type, mets: Type](baseType: TypeRepr): TypeRepr = {
        Type.of[mels] match
          case '[mel *: melTail] => {
            Type.of[mets] match {
              case '[head *: tail] => {
                val label = Type.valueOfConstant[mel].get.toString
                Expr.summon[DBFieldLike[head]] match {
                  case Some('{ $m: DBFieldLike[head]}) => {
                    val withField = Refinement(baseType, label, TypeRepr.of[DBField{type Underlying = head}])
                    // val withTableField = Refinement(withField, label, TypeRepr.of[DBField{type Underlying = head}])
                    recur[melTail, tail](withField)
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
    val tableName= TypeRepr.of[T].typeSymbol.name
    val tableNameExpr = Expr(tableName)

    Expr.summon[Mirror.ProductOf[T]].get match {
      case '{ $m: Mirror.ProductOf[T] {type MirroredElemLabels = mels; type MirroredElemTypes = mets } } =>
        val tableType = recur[mels,mets](TypeRepr.of[Table[T]])
        val tablesType = Refinement(TypeRepr.of[Tables], tableName, tableType)

        tablesType.asType match {
              case '[tpe] =>
                '{
                  val table = summonInline[Table[T]]
                  val tables = new Tables(Map(${tableNameExpr} -> table))
                  tables.asInstanceOf[tpe]
                }
        }
      case e => report.error(e.show);???
    }
  }
}
