package io.github.liewhite.sql

import scala.compiletime.*

// 类型以及约束
enum ColumnType{
  case Integer()
  case BigInt()
  case Serial
  case VarChar(length: Int)
  case Text()
  case Bool
}

trait DBValue{
  type Underlying
  def toExpr:String

  inline def eql(that:DBValue): Boolean = {
    if(typeCompatibie[Underlying, that.Underlying]){
      true
    }else{
      error("type error")
    }
  }

  inline def typeCompatibie[T1,T2]: Boolean = {
    if(constValue[T1] == constValue[T2]){
      true
    }else{
      error("type error")
    }
  }
}

object DBValue{
  given Conversion[Int, DBValue] with
    def apply(s: Int): DBValue{type Underlying = "integer"} = new DBValue{
      type Underlying = "integer"
      def toExpr = s.toString
    }
  given Conversion[String, DBValue] with
    def apply(s: String): DBValue{type Underlying = "string"} = new DBValue{
      type Underlying = "string"
      def toExpr = s
    }
}

class Column(val tableName:String, val name:String,val tp: ColumnType) extends DBValue {
  def toExpr = Vector(tableName, name).mkString(".")
}

trait ColumnLike[T]{
  type Underlying
  def columnType: ColumnType 
}

object ColumnLike{
  given ColumnLike[Int] with {
    type Underlying = "integer"
    def columnType = ColumnType.Integer()
  }

  given ColumnLike[String] with {
    type Underlying = "string"
    def columnType = ColumnType.VarChar(255)
  }
}
