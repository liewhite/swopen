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
  inline def eql[THAT <: T](that: THAT)(using converter: DBValueConverter[THAT]): ConditionExpr = {
    ConditionExpr("(? = ?)", this.toExpr, converter.convert(that).toExpr)
  }

  def toExpr: Expr
}

class Column[T](val tableName:String, val name:String,val tp: DBType) extends DBValue[T] {
  def toExpr: Expr = Expr(Vector(tableName,name).mkString("."))
}

trait DBValueConverter[T]{
  def dbRepr: DBType
  def convert(t:T):DBValue[T]
}

object DBValueConverter{
  // given [T]: DBValueConverter[DBValue[T]] with {
  //   def dbRepr: DBRepr[Int] = new DBRepr[Int] {def tp = DBType.Integer()}
  //   def convert(t:DBValue[T]):DBValue[DBValue[T]] = t.asInstanceOf
  // }

  given DBValueConverter[Int] with {
    def dbRepr: DBType = DBType.Integer()
    def convert(t:Int):DBValue[Int] = {
      new DBValue[Int]{
        def toExpr:Expr = Expr(t.toString)
      }
    }
  }

  given DBValueConverter[Long] with {
    def dbRepr: DBType = DBType.BigInt()
    def convert(t:Long):DBValue[Long] = {
      new DBValue[Long]{
        def toExpr:Expr = Expr(t.toString)
      }
    }
  }

  given DBValueConverter[String] with {
    def dbRepr: DBType = DBType.VarChar(255)
    def convert(t:String):DBValue[String] = {
      new DBValue[String]{
        def toExpr:Expr = Expr(t)
      }
    }
  }

  given DBValueConverter[Boolean] with {
    def dbRepr: DBType = DBType.Bool
    def convert(t:Boolean):DBValue[Boolean] = {
      new DBValue[Boolean]{
        def toExpr:Expr = Expr(t.toString)
      }
    }
  }
}