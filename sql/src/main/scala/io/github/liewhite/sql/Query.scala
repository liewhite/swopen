package io.github.liewhite.sql



class JoinedQuery(var tables: Map[String, Query]) extends Selectable{
  def selectDynamic(name: String): Any ={
    tables(name)
  }
  transparent inline def join[T <: JoinedQuery](t:T) = {
    new JoinedQuery(this.tables ++ t.tables).asInstanceOf[this.type & T]
  }
}

class Query(val table:Table[_]) extends Selectable{
  def selectDynamic(name: String): Any ={
    table.columns(name)
  }
}

object Query{
}


