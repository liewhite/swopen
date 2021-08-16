package io.github.liewhite.sql


class Expr(val expr:String, val args:Vector[Expr]){
  def render: String = {
    expr.format(args.map(_.render))
  }
}

class ConditionExpr(expr:String, args:Vector[Expr]) extends Expr(expr, args){
  def and(cond: ConditionExpr): ConditionExpr = {
    ConditionExpr(s" ($render and ${cond.render}) ", Vector.empty )
  }
  def or(cond: ConditionExpr): ConditionExpr = {
    ConditionExpr(s" ($render or ${cond.render}) " , Vector.empty)
  }
  def not: ConditionExpr = {
    ConditionExpr(s" (not($render)) " , Vector.empty)
  }
}
