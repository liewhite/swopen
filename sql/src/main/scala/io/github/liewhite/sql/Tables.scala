package io.github.liewhite.sql

import scala.compiletime.erasedValue

enum JoinType extends SqlExpr[JoinType]{
  case Left
  case Right
  case Inner
  case Full

  def toSql: String = {
    this match {
      case Left => "left join"
      case Right => "right join"
      case Inner => "join"
      case Full => "full join"
    }
  }
} 

case class QueryStmt[R](sql: String)

case class SelectField[T](name:String)


case class Join(t: JoinType, table: Table[_], cond: Option[Condition])

case class SelectQuery(
    mainTable: Table[_],
    tables: Map[String, Table[_]],
    joins: Vector[Join] = Vector.empty,
    where: Option[Condition] = None
) extends Selectable, SqlExpr[_] {

  def toSql: String = {
    val select = s"SELECT * FROM ${mainTable.tableName}"
    val joinStr = joins.foldLeft(select)((result,item) => {
      val joined = s" ${item.t.toSql} ${item.table.tableName}"
      joined + " " + (item.cond match {
        case Some(on) => s"on ${on.toSql}"
        case None => joined
      })
    })
    val where = this.where match {
      case Some(w) => "where " + w.toSql
      case None => ""
    }
    Vector(select, joinStr, where).mkString(" ")
  }

  def selectDynamic(name: String): Any = {
    tables(name)
  }
}

trait SqlExpr[T]{
  def toSql: String 
}

case class CountExpr[T](expr: SqlExpr[_]) extends SqlExpr[Long] {
  def toSql: String = s"count(${expr.toSql})"
}

object SelectQuery{
  type RESULT[R] = R match {
    case SqlExpr[t] *: tail => t *: RESULT[tail]
    case EmptyTuple => EmptyTuple
  }

  extension [T<: SelectQuery](t:T){
    inline def join[T2 <: SelectQuery](
        joinType: JoinType,
        t2: T2,
        on: Option[T2 & T => Condition] = None
    ): T & T2 = {
      val newTables = t.copy(tables = t.tables ++ t2.tables)
      val newJoins = t.joins.appended(Join(joinType,t.mainTable, on.map(item => item(newTables.asInstanceOf))))
      newTables.copy(joins = newJoins).asInstanceOf[T & T2]
    }

    inline def where(cond: T => Condition): T = {
      t.copy(where = Some(cond(t))).asInstanceOf[T]
    }


    inline def select[R](cond: T => R): QueryStmt[RESULT[R]] = {
      QueryStmt(t.toSql).asInstanceOf[QueryStmt[RESULT[R]]]
    }
  }

}
