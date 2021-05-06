import scala.quoted.*
import scala.compiletime._
import scala.deriving.*

def impl[T:Type](using Quotes):Expr[Any] = 
  import quotes.reflect._
  val tpe: TypeRepr = TypeRepr.of[T]
  println(tpe.typeSymbol.fullName)
  Expr(123)

inline def myMacro[T]: Any = 
  ${impl[T]}