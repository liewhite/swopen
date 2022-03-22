package io.github.liewhite.sql

import java.sql.ResultSet
import java.sql.PreparedStatement
import java.sql.SQLException
import org.jooq.DataType
import org.jooq.impl.BuiltInDataType
import org.jooq.impl.SQLDataType

abstract class DBDsl {
  val nameQuote: String
}

object DBDslMySQL extends DBDsl {
  val nameQuote: String = "`"
}
object DBDslPostgres extends DBDsl {
  val nameQuote: String = "\""
}

trait QueryResult[T] {
  def fromResultSet(set: ResultSet): Either[Exception, T]
}

object QueryResult {}

case class Field(
    modelName: String,
    // scala case class field name
    fieldName: String,
    // database table name
    colName: String,
    unique: Boolean,
    default: Option[Any],
    t: TField[Any]
) {
  override def toString: String = {
    s"""${colName} unique: ${unique}, default:${default} nullable: ${t.nullable}"""
  }
  def queryName(engine: DBDsl): String ={
    val quote = engine.nameQuote
     s"${quote}${modelName}${quote}.${quote}${colName}${quote}"
  }
}

trait TField[T] {
  // option type with true
  def nullable: Boolean = false
  // jooq datatype
  def dataType: DataType[_]
  def scan(obj: Any): Either[Exception, T]
  def value(value: T): Either[Exception, Any]
}

object TField {
  given[T](using t: TField[T]): TField[Option[T]] with {
    override def nullable: Boolean = true
    def dataType: DataType[_] = t.dataType

    def scan(obj: Any): Either[Exception, Option[T]] = {
      if(obj == null) {
        Right(None)
      }else{
        t.scan(obj) match {
          case Right(v) => Right(Some(v))
          case Left(e) => Left(e)
        }
      }
    }
    def value(v: Option[T]): Either[Exception, Any] = {
      v match {
        case Some(s) => t.value(s)
        case None => null
      }
    }
  }

  given TField[Int] with {
    def dataType: DataType[_] = SQLDataType.INTEGER

    // obj comes from jdbc resultSet.getObject
    def scan(obj: Any): Either[Exception, Int] = {
      obj match {
        case i: Int => Right(i)
        case o      => Left(Exception("unknwon value:" + o.toString))
      }
    }
    // return value used for preparedStatement.setObject
    def value(v: Int): Either[Exception, Any] = {
      Right(v)
    }
  }
}
