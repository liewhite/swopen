package io.github.liewhite.sql

import scala.compiletime.*
import scala.quoted.*
import scala.deriving.Mirror

import shapeless3.deriving.{K0, Continue, Labelling}
import io.github.liewhite.common.SummonUtils.summonAll
import io.github.liewhite.common.{RepeatableAnnotation, RepeatableAnnotations}
import io.github.liewhite.sql.annotation
import io.github.liewhite.json.typeclass.DefaultValue
import org.jooq

case class Index(name: String, cols: Vector[String], unique: Boolean)

// migrate时， 先拿到meta，
// 然后将diff apply 到db
// 再从database meta 恢复出table,用作后续jooq的操作
trait Table[T] extends Selectable {
  def tableName: String
  def indexes: Vector[Index]
  // def colsMap: Map[String, ]
  def columns: Vector[Field[_]]
  def columnsMap: Map[String, Field[_]] =
    columns.map(item => (item.fieldName, item)).toMap

  def table(using conn: Connection): jooq.Table[_] = {
      conn.metaCache.getTables(tableName).get(0)
  }
  
  def selectDynamic(name: String): Any = {
    columnsMap(name)
  }
}

object Table {
  given (using conn: Connection) : Conversion[Table[_], jooq.Table[_]] with {
    def apply(t: Table[_]): jooq.Table[_] = {
      conn.metaCache.getTables(t.tableName).get(0)
    }

  }
  inline given derived[A](using
      gen: Mirror.ProductOf[A],
      labelling: Labelling[A],
      primaryKey: RepeatableAnnotations[annotation.Primary, A],
      columnName: RepeatableAnnotations[annotation.ColumnName, A],
      index: RepeatableAnnotations[annotation.Index, A],
      unique: RepeatableAnnotations[annotation.Unique, A],
      defaultValue: DefaultValue[A]
  ): Table[A] = {
    val defaults = defaultValue.defaults
    val columnTypes = summonAll[TField, gen.MirroredElemTypes]
    // snack case
    val tName = "[A-Z\\d]".r.replaceAllIn(
      labelling.label,
      { m =>
        "_" + m.group(0).toLowerCase()
      }
    )
    val fieldNames = labelling.elemLabels.toVector
    val renames =
      columnName().map(item => if (item.isEmpty) None else Some(item(0).name))
    val names = renames.zipWithIndex.map {
      case (rename, index) => {
        rename match {
          case Some(v) => v
          case None    => fieldNames(index)
        }
      }
    }
    val primaries = primaryKey().map(item => if (item.isEmpty) false else true)
    val uniques = unique().map(item => if (item.isEmpty) false else true)

    val idxes = index().zipWithIndex
      .filter(!_._1.isEmpty)
      .map(item => {
        item._1.map(i =>
          (
            i.copy(priority = if (i.priority != 0) i.priority else item._2),
            names(item._2)
          )
        )
      })
      .flatten

    val groupedIdx = idxes
      .groupBy(item => item._1.name)
      .map {
        case (name, items) => {
          Index(name, items.map(_._2).toVector, items(0)._1.unique)
        }
      }
      .toVector

    val cols = fieldNames.zipWithIndex.map {
      case (name, index) => {
        val tp = columnTypes(index)
        val unique = uniques(index)
        val default = defaults.get(fieldNames(index))

        Field(tName, name, names(index), unique, default, tp)
      }
    }

    new Table {
      def tableName = tName
      def indexes = groupedIdx
      def columns = cols.toVector
    }
  }

  transparent inline def apply[T] = {
    ${ queryImpl[T] }
  }

  private def queryImpl[T: Type](using Quotes): Expr[Any] = {
    import quotes.reflect.*

    def recur[mels: Type, mets: Type](baseType: TypeRepr): TypeRepr = {
      Type.of[mels] match
        case '[mel *: melTail] => {
          Type.of[mets] match {
            case '[head *: tail] => {
              val label = Type.valueOfConstant[mel].get.toString
              val withField =
                Refinement(baseType, label, TypeRepr.of[Field[head]])
              recur[melTail, tail](withField)
            }
          }
        }
        case '[EmptyTuple] => baseType
    }
    val tableName = TypeRepr.of[T].typeSymbol.name
    val tableNameExpr = Expr(tableName)

    Expr.summon[Mirror.ProductOf[T]].get match {
      case '{
            $m: Mirror.ProductOf[T] {
              type MirroredElemLabels = mels; type MirroredElemTypes = mets
            }
          } =>
        val tableType = recur[mels, mets](TypeRepr.of[Table[T]])

        tableType.asType match {
          case '[tpe] =>
            '{
              val table = summonInline[Table[T]]
              table.asInstanceOf[tpe]
            }
        }
      case e => report.error(e.show); ???
    }
  }
}
