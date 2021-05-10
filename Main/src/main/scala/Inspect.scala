// import scala.quoted.*
// import scala.compiletime._
// import scala.deriving.*

// def impl[T:Type](using Quotes):Expr[A[T]] = 
//   import quotes.reflect._
//   val tpe: TypeRepr = TypeRepr.of[T]
//   tpe match
//     case OrType(left,right) =>
//       val lefta = left.asType
//       val righta = right.asType
//       '{new A[T]{
//           def f:String = {
//             val sl = summon[A[lefta]]
//             val sr = summon[A[r]]
//             sl.f + "|" + sr.f
//           }
//         }
//       }
//     case _ => ???

// inline given myMacro[T]: A[T]  = 
//   ${impl[T]}
