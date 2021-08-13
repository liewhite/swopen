package io.github.liewhite.sql

import scala.compiletime.*
import org.scalafmt.util.LogLevel.warn


enum FieldSpec{
  case IntegerField()
  case BigInt()
  case Serial

  case VarChar(length: Int)
  case Text()

  case Bool
}

trait Field[T]{
  type Underlying

  var name: String = ""
  var tableName: String = ""

  def withName(n:String): this.type = {
    this.name = n
    this
  }
  def withTableName(tn: String): this.type = {
    this.tableName = tn
    this
  }

  def fullName = Vector(tableName, name).mkString(".")

  def fieldType: FieldSpec

  inline def < [TT](t:TT)(using s: Field[TT]): Boolean = {
    // todo 暂时只允许相同类型进行比较，后续应该允许子类型
    // todo 禁止非比较类型调用， 比如bool
    if(constValue[s.Underlying] == constValue[this.Underlying]){
      true
    }else{
      error("type error")
    }
  }
}

object Field{
  given Field[Int] with {
    type Underlying = "Integer"
    def fieldType = FieldSpec.IntegerField()
  }

  given Field[Long] with {
    type Underlying = "Integer"
    def fieldType = FieldSpec.IntegerField()
  }

  given Field[String] with {
    type Underlying = "String"
    def fieldType = FieldSpec.VarChar(255)
  }
}