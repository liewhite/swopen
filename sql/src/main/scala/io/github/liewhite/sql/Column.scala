package io.github.liewhite.sql

import scala.compiletime.*

// 类型以及约束
enum DBType{
  case Integer()
  case BigInt()
  case Serial
  case VarChar(length: Int)
  case Text()
  case Bool
}

trait DBValue[T]{

  // inline def eql[THAT <: T](that: THAT)(using converter: DBValueConverter[THAT]): ConditionExpr = {
  //   ConditionExpr("(? = ?)", this.toExpr, converter.toDBValue(that).toExpr)
  // }

  def toExpr: SqlExpr
}

class Column[T](val tableName:String, val name:String,val tp: DBType) extends DBValue[T] {
  def toExpr: SqlExpr = SqlExpr(Vector(tableName,name).mkString("."))
}

trait DBValueConverter[T]{
  def dbRepr: DBType
  def toDBValue(t: T): DBValue[T]
  // def fromDBValue(t: DBValue[T]): T
}

object DBValueConverter{

  given DBValueConverter[Int] with {
    def dbRepr: DBType = DBType.Integer()
    def toDBValue(t:Int):DBValue[Int] = {
      new DBValue[Int]{
        def toExpr:SqlExpr = SqlExpr(t.toString)
      }
    }
  }

  given DBValueConverter[Long] with {
    def dbRepr: DBType = DBType.BigInt()
    def toDBValue(t:Long):DBValue[Long] = {
      new DBValue[Long]{
        def toExpr:SqlExpr = SqlExpr(t.toString)
      }
    }
  }

  given DBValueConverter[String] with {
    def dbRepr: DBType = DBType.VarChar(255)
    def toDBValue(t:String):DBValue[String] = {
      new DBValue[String]{
        def toExpr:SqlExpr = SqlExpr(t)
      }
    }
  }

  given DBValueConverter[Boolean] with {
    def dbRepr: DBType = DBType.Bool
    def toDBValue(t:Boolean):DBValue[Boolean] = {
      new DBValue[Boolean]{
        def toExpr:SqlExpr = SqlExpr(t.toString)
      }
    }
  }
}