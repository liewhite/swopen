package io.github.liewhite.sql

trait Expr[T] {
  def render: String
}
