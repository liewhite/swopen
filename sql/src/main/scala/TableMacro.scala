import scala.quoted.*

trait Table[T] extends reflect.Selectable {
}

object Table {
  transparent inline def table[T]: Table[T] = {
    ${tableImpl[T]}
  }
  def tableImpl[T:Type](using q: Quotes): Expr[Table[T]] = {
    import q.reflect._
    val expr = '{
      new Table[T]{
        val id: Int = 3
      }
    }
    println(expr.asTerm)
    expr
  }
}