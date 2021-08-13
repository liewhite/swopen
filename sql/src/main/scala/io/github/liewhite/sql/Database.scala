package io.github.liewhite.sql

import scala.compiletime.*
import scala.quoted.*
import scala.deriving.Mirror

class Props[T](using table:Table[T]) extends Selectable:
  def selectDynamic(name: String): Any ={
    table.columns(name)
  }

transparent inline def props[T] =
  ${ propsImpl[T] }

private def propsImpl[T: Type](using Quotes): Expr[Any] =
  import quotes.reflect.*

  def recur[mels : Type, mets:Type](baseType: TypeRepr): TypeRepr = {
      Type.of[mels] match
        case '[mel *: melTail] => {
          Type.of[mets] match {
            case '[head *: tail] => {
              val label = Type.valueOfConstant[mel].get.toString
              Expr.summon[Field[head]] match {
                case Some('{ $m: Field[head] {type Underlying = u}}) => {
                  recur[melTail, tail](Refinement(baseType, label, TypeRepr.of[Field[head]{type Underlying = u}]))
                }
                case None => {
                  report.error("Field implementation not found:")
                  ???
                }
                case _ => {
                  report.error("Unknown error when summon Field[head]")
                  ???
                }
              }
            }
          }
        }
        case '[EmptyTuple] => baseType
  }

  Expr.summon[Mirror.ProductOf[T]].get match {
    case '{ $m: Mirror.ProductOf[T] {type MirroredElemLabels = mels; type MirroredElemTypes = mets } } =>
      recur[mels,mets](TypeRepr.of[Props]).asType match {
            case '[tpe] =>
              '{
                val table = summonInline[Table[T]]
                val p = Props[T](using table)
                p.asInstanceOf[tpe]
              }
      }
  }