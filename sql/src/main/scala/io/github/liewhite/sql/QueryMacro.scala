package io.github.liewhite.sql

import scala.compiletime.*
import scala.quoted.*
import scala.deriving.Mirror

import shapeless3.deriving.{K0, Continue, Labelling}
import io.github.liewhite.common.SummonUtils.summonAll

object QueryMacro{
  // transparent inline def join[T1 <: Query[_],T2 <: Query[_]](t1:T1,t2:T2)  = {
  //   ${ QueryMacro.joinImpl('t1,'t2) }
  // }

  // def joinImpl[T1:Type,T2:Type](t1:Expr[T1],t2: Expr[T2])(using quotes: Quotes): Expr[Any] = {
  //   import quotes.reflect.*
  //   val baseType = TypeRepr.of[MultiTableQuery[(T1,T2)]]
  //   val t1TypeRepr = TypeRepr.of[T1]
  //   val t2TypeRepr = TypeRepr.of[T2]

  //   val t1Type = Refinement(baseType, t1TypeRepr.typeSymbol.name, TypeRepr.of[T1])
  //   val t2Type = Refinement(t1Type, t2TypeRepr.typeSymbol.name, TypeRepr.of[T2])

  //   t2Type.asType match {
  //     case '[t] => {
  //       println(t2Type.show)
  //       '{
  //         // val table1 = summonInline[Table[T1]]
  //         // val table2 = summonInline[Table[T2]]
  //         // val tableMap = Map(${t1}.table.tableName -> ${t1}.table,${t2}.table.tableName -> ${t2}.table)
  //         // val p = new MultiTableQuery[(T1,T2)](tableMap)
  //         // p.asInstanceOf[t]
  //       }

  //     }
  //   }
  // }
}