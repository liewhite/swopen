package io.github.liewhite.sql

enum DBFieldType{
  case Integer()
  case BigInt()
  case Serial
  case VarChar(length: Int)
  case Text()
  case Bool
}

case class DBField(val fieldName: String, val tableName:String, val fieldType: DBFieldType)

trait DBFieldLike[T]{
  def toField(fieldName:String, tableName:String): DBField
}
object DBFieldLike{

  given DBFieldLike[Int] with {
    def toField(fieldName:String, tableName:String): DBField = DBField(fieldName,tableName, DBFieldType.Integer())
  }

  given DBFieldLike[String] with {
    def toField(fieldName:String, tableName:String): DBField = DBField(fieldName,tableName, DBFieldType.VarChar(255))
  }

}