package io.github.liewhite.sql

import scala.quoted.*

trait Query[T]{

}

object Builder{
  transparent inline def query[T] = {
    ${queryImpl[T]}
  }

  def queryImpl[T:Type](using q: Quotes): Expr[Any] = {
    import q.reflect.*
    val o = '{
      new Query[T]{
        def a:Int = 1
        def b:String = "asd"
        def c = true
      }
    }
    println(o.asTerm)
    o
  }
}