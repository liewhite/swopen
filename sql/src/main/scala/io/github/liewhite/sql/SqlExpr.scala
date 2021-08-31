package io.github.liewhite.sql


trait SqlExpr[T]{
  def toSql: String 
}

case class CountExpr[T](expr: SqlExpr[_]) extends SqlExpr[Long] {
  def toSql: String = s"count(${expr.toSql})"
}

class Condition(template:String, parameters: SqlExpr[_]* ) extends SqlExpr[Boolean]{
  def toSql: String = {
    template.foldLeft(("", 0))((result,item) => {
        if(item == '?'){
          (result._1+ parameters(result._2).toSql, result._2 + 1)
        }else{
          (result._1 + item.toString, result._2)
        }
    })._1
  }
}