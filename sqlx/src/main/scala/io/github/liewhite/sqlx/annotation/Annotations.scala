package io.github.liewhite.sqlx.annotation

// name in db
case class ColumnName(name:String) extends scala.annotation.StaticAnnotation

// primary key
case class Primary() extends scala.annotation.StaticAnnotation

// index, maybe unique, annotate Index with same name will create a composite index
case class Index(name:String, unique:Boolean = false, priority:Int=0) extends scala.annotation.StaticAnnotation

// unique constraint
case class Unique() extends scala.annotation.StaticAnnotation
case class Length(l: Int) extends scala.annotation.StaticAnnotation