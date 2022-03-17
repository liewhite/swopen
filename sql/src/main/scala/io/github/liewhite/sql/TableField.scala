package io.github.liewhite.sql

import java.sql.ResultSet
import java.sql.PreparedStatement
import java.sql.SQLException

enum DBEngine {
  case Mysql
  case Postgres
}

trait QueryResult[T] {
  def fromResultSet(set: ResultSet): Either[Exception, T]
}

object QueryResult {}

case class Field[T](name: String, t: TField[T])

trait TField[T] {
  def dataType(engine: DBEngine): String
  def scan(name: String, row: ResultSet): Either[Exception, T]
  def value(value: T): Either[Exception, Any]
}

object TField {
  given TField[Int] with {
    def dataType(engine: DBEngine): String = "int"
    def scan(name: String, row: ResultSet): Either[Exception, Int] = {
      try {
        Right(row.getInt(name))
      } catch {
        case e: SQLException => Left(e)
      }
    }
    def value(v: Int): Either[Exception, Any] = {
      Right(v)
    }
  }
}
