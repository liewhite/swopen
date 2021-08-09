package io.github.liewhite.sql


enum FieldSpec{
  case IntegerField()
  case BigInt()
  case Serial

  case VarChar(length: Int)
  case Text()

  case Bool
}

trait Field[T]{
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
}

object Field{
  given Field[Int] with {
    def fieldType = FieldSpec.IntegerField()
  }
  given Field[String] with {
    def fieldType = FieldSpec.VarChar(255)
  }
}