package io.github.liewhite.sql

import scala.compiletime.*
import scala.quoted.*
import scala.deriving.Mirror

import shapeless3.deriving.{K0, Continue, Labelling}
import io.github.liewhite.common.SummonUtils.summonAll

class MultiTableQuery[TS <: Tuple](val tables: Map[String, Table[_]]) extends Selectable {
  def selectDynamic(name: String): Any = {
    tables(name)
  }
}

object MultiTableQuery{
}