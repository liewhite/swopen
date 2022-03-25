package io.github.liewhite.sql

import org.jooq.{SQLDialect, DSLContext}
import org.jooq.impl.DSL
import org.jooq
import java.sql.DriverManager
import scala.collection.mutable
import org.jooq.impl.SQLDataType
import org.jooq.CreateTableColumnStep
import org.jooq.CreateTableConstraintStep

class Connection(val jooqConn: DSLContext) {
  // todo 应当在migration后再次获取
  var metaCache: jooq.Meta = jooqConn.meta()
  val tables: mutable.Map[String, Table[_]] = mutable.Map.empty

  def getTable(name: String): Option[jooq.Table[_]] = {
    val result = metaCache.getTables(name)
    if (result.isEmpty) {
      None
    } else {
      Some(result.get(0))
    }
  }

  def migration = {
    // 此时meta已经获取到
    tables.values.foreach(table => {
      val tableName = table.tableName
      getTable(tableName) match {
        case None    => createTable(table)
        case Some(t) => updateTable(table, t)
      }
    })
  }

  // 拿到table, 然后保存到
  def registerMigration[T](using t: Table[T]) = {
    tables.put(t.tableName, t)
  }

  def createTable(table: Table[_]) = {
      val withCols: CreateTableConstraintStep =
        table.columns.foldLeft(jooqConn.createTable(table.tableName))(
          (b, col) => {
            var datatype = col.t.dataType.asInstanceOf[jooq.DataType[Any]]
            if (col.default.isDefined) {
              datatype = datatype.defaultValue(col.default.get.asInstanceOf[Any])
            }
            datatype = datatype.nullable(col.t.nullable)
            if (col.colName == "id") {
              datatype = datatype.identity(true)
            }
            var c = b.column(col.colName, datatype)
            c
          }
        )
      val withUniques: CreateTableConstraintStep =
        table.columns.foldLeft(withCols)((b, col) => {
          if (col.unique) {
            b.unique(col.colName)
          } else {
            b
          }
        })
      val withPrimaryKey: CreateTableConstraintStep =
        table.columnsMap.get("id") match {
          case Some(id) => withUniques.primaryKey(id.colName)
          case None     => throw Exception("missing id column")
        }
      println(withPrimaryKey.getSQL)
      withPrimaryKey.execute

      table.indexes.foreach(idx => {
        if (idx.unique) {
          jooqConn
            .createUniqueIndex(idx.name)
            .on(table.tableName, idx.cols*)
            .execute
        } else {
          jooqConn.createIndex(idx.name).on(table.tableName, idx.cols*).execute
        }
      })
  }

  def updateTable(table: Table[_], current: jooq.Table[_]) = ???
}

object Connection {
  def apply(jdbcUrl: String, username: String, password: String): Connection = {
    val conn = DriverManager.getConnection(jdbcUrl, username, password);
    val create = DSL.using(conn);
    new Connection(create)
  }
}
