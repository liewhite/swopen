package io.github.liewhite.sql

enum TableFieldType{
  case Integer()
  case BigInt()
  case Serial
  case VarChar(length: Int)
  case Text()
  case Bool
}

case class TableField[T](val fieldName: String, val tableName:String, val fieldType: TableFieldType) extends SqlExpr[T]{
  def toSql: String = Vector(tableName, fieldName).mkString(".")

  inline def eql[THAT <: T](that: TableField[THAT]): Condition = {
    Condition("? = ?", this, that)
  }
}

trait TableFieldLike[T]{
  def toField(fieldName:String, tableName:String): TableField[T]
}

object TableFieldLike{

  given TableFieldLike[Int] with {
    def toField(fieldName:String, tableName:String): TableField[Int] = TableField(fieldName,tableName, TableFieldType.Integer())
  }

  given TableFieldLike[String] with {
    def toField(fieldName:String, tableName:String): TableField[String] = TableField(fieldName,tableName, TableFieldType.VarChar(255))
  }

  given TableFieldLike[Boolean] with {
    def toField(fieldName:String, tableName:String): TableField[Boolean] = TableField(fieldName,tableName, TableFieldType.Bool)
  }
}