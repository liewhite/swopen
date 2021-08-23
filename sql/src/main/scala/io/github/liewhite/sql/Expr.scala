package io.github.liewhite.sql

import cats.syntax.apply

trait SqlExpr {
  def render: String
}

object SqlExpr{
  def apply(expr:String, args: SqlExpr*): SqlExpr = new SqlExpr{
    def render:String = expr
  }
}


case class PlainExpr(expr:String, args: SqlExpr*){
  def render: String = {
    expr.format(args.map(_.render))
  }
}

case class OperandExpr(operandRepr:String) extends SqlExpr{
  def render: String = operandRepr
}


