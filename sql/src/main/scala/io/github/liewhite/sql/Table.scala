package io.github.liewhite.sql


import shapeless3.deriving.{K0, Continue, Labelling}
import scala.compiletime.*
import scala.deriving.Mirror
import io.github.liewhite.common.SummonUtils.summonAll

trait Table[T]{
  def tableName: String
  def columns: Map[String,Field[_]]
}

object Table{
  inline def derived[A](using gen: Mirror.ProductOf[A],labelling: Labelling[A]): Table[A] =
    val items = summonAll[Field, gen.MirroredElemTypes]
    val elemsWithName = labelling.elemLabels.zip(items).map{
      case (label, item) => item
    }
    new Table{
      def tableName = labelling.label
      def columns = labelling.elemLabels.zip(elemsWithName).toMap
    }

}