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
trait DBRepr[T]{
  def tp: DBType
}
object DBRepr{
  given DBRepr[Int] with{
    def tp = DBType.Integer()
  }
  given DBRepr[Long] with{
    def tp = DBType.Integer()
  }

  given DBRepr[String] with{
    def tp = DBType.VarChar(255)
  }
}

class Column[T](val tableName:String, val name:String,val tp: DBType) {
  inline def eql[THAT <: T](that: THAT)(using converter: LiterialConverter[THAT]): Expr = {
    ConditionExpr("(? = ?)", Vector(this.toExpr, converter.convert(that).toExpr))
  }
  def toExpr: Expr = Expr(Vector(tableName,name).mkString("."),Vector.empty)
}

trait LiterialConverter[T]{
  def convert(t:T):Column[T]
}

object LiterialConverter{
  given [T]: LiterialConverter[Column[T]] with {
    override def convert(t:Column[T]):Column[Column[T]] = t.asInstanceOf
  }

  given LiterialConverter[Int] with {
    def convert(t:Int):Column[Int] = {
      new Column[Int]("","", DBType.Integer()){
        override def toExpr:Expr = Expr(t.toString, Vector.empty)
      }
    }
  }

  given LiterialConverter[Long] with {
    def convert(t:Long):Column[Long] = {
      new Column[Long]("","", DBType.Integer()){
        override def toExpr:Expr = Expr(t.toString, Vector.empty)
      }
    }
  }

  given LiterialConverter[String] with {
    def convert(t:String):Column[String] = {
      new Column[String]("","", DBType.VarChar(255)){
        override def toExpr:Expr = Expr(t, Vector.empty)
      }
    }
  }

  given LiterialConverter[Boolean] with {
    def convert(t:Boolean):Column[Boolean] = {
      new Column[Boolean]("","", DBType.Bool){
        override def toExpr:Expr = Expr(t.toString, Vector.empty)
      }
    }
  }
}