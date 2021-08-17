package io.github.liewhite.sql


class Expr(val expr:String, val args: Expr*){
  def render: String = {
    expr.format(args.map(_.render))
  }
}

class ConditionExpr(expr:String, args:Expr*) extends Expr(expr, args:_*){
  def and(cond: ConditionExpr): ConditionExpr = {
    ConditionExpr(s" ($render) and (${cond.render}) ")
  }
  def or(cond: ConditionExpr): ConditionExpr = {
    ConditionExpr(s" ( $render) or (${cond.render}) " )
  }
  def not: ConditionExpr = {
    ConditionExpr(s" (not $render) ")
  }
}
