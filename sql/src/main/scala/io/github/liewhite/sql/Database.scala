package io.github.liewhite.sql

import scala.compiletime.*
import scala.quoted.*
import scala.deriving.Mirror

class Props extends Selectable:
  def selectDynamic(name: String): Any =
    "prop for " + name

transparent inline def props[T] =
  ${ propsImpl[T] }

private def propsImpl[T: Type](using Quotes): Expr[Any] =
  import quotes.reflect.*

  def recur[mels : Type](baseType: TypeRepr): TypeRepr = {
      Type.of[mels] match
        case '[mel *: melTail] =>
          val label = Type.valueOfConstant[mel].get.toString
          recur[melTail](Refinement(baseType, label, TypeRepr.of[String]))
        case '[EmptyTuple] => baseType
  }

  Expr.summon[Mirror.ProductOf[T]].get match
    case '{ $m: Mirror.ProductOf[T] {type MirroredElemLabels = mels; type MirroredElemTypes = mets } } =>
      recur[mels](TypeRepr.of[Props]).asType match {
            case '[tpe] =>
              val res = '{
                val p = Props()
                p.asInstanceOf[tpe]
              }
              println(res.show)
              res
      }