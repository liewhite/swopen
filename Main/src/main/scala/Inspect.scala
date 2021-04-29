import scala.quoted.*
import scala.compiletime._
import scala.deriving.*

def impl(x: Expr[Int])(using Quotes):Expr[Any] = 
  import quotes.reflect._
  println(s"expr: ${x.show}")
  if x.value == None then 
    "ok"
  else
    report.error(s"-----------------report error--------------------${x.value}")
  x

inline def myMacro[T](using inline x:Int): Any = 
  ${impl('x)}