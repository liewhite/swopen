package io.github.liewhite.sql

class Tables(var tables: Map[String, Table[_]]) extends Selectable{
  def selectDynamic(name: String): Any ={
    tables(name)
  }
  transparent inline def join[T <: Tables](t:T) = {
    new Tables(this.tables ++ t.tables).asInstanceOf[this.type & T]
  }
  inline def where(cond: this.type => Condition): this.type = {
    this
  }
}