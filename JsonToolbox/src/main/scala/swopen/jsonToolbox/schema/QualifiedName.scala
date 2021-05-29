// package swopen.jsonToolbox.schema
// import scala.quoted.*
// import scala.compiletime._
// import scala.deriving.*

// trait QualifiedName[T]:
//   def fullName: String
// end QualifiedName

// object QualifiedName:
//   def mkQn[T](name:String): QualifiedName[T] = 
//     new QualifiedName[T]:
//       def fullName = name

//   def impl[T:Type](using Quotes):Expr[QualifiedName[T]] = 
//     import quotes.reflect._
//     val tpe: TypeRepr = TypeRepr.of[T]
//     val name = Expr(tpe.typeSymbol.fullName)
//     '{mkQn[T]($name)}

//   inline given[T]: QualifiedName[T] = 
//     ${impl[T]}