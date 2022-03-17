package io.github.liewhite.sql

import scala.compiletime.*
import scala.quoted.*
import scala.deriving.Mirror

import shapeless3.deriving.{K0, Continue, Labelling}
import io.github.liewhite.common.SummonUtils.summonAll

trait Table[T] extends Selectable{
  def tableName: String
  def columnsMap: Map[String,Field[_]]
  def selectDynamic(name: String): Any ={
    columnsMap(name)
  }
}

object Table{
  inline given derived[A](using gen: Mirror.ProductOf[A],labelling: Labelling[A]): Table[A] = {
    val columnTypes = summonAll[TField, gen.MirroredElemTypes]
    val tableName = labelling.label
    val cols = labelling.elemLabels.zip(columnTypes).map{
      case (k,v) => (k, Field(k,v))
    }.toMap
    // todo 获取各种annotations
    new Table{
      def tableName = labelling.label
      def columnsMap = cols
    }
  }

  transparent inline def apply[T] ={
    ${ queryImpl[T]}
  }

  private def queryImpl[T: Type](using Quotes): Expr[Any] = {
    import quotes.reflect.*

    def recur[mels : Type, mets: Type](baseType: TypeRepr): TypeRepr = {
        Type.of[mels] match
          case '[mel *: melTail] => {
            Type.of[mets] match {
              case '[head *: tail] => {
                val label = Type.valueOfConstant[mel].get.toString
                val withField = Refinement(baseType, label, TypeRepr.of[Field[head]])
                recur[melTail, tail](withField)
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

        tableType.asType match {
              case '[tpe] =>
                '{
                  val table = summonInline[Table[T]]
                  table.asInstanceOf[tpe]
                }
        }
      case e => report.error(e.show);???
    }
  }
}
