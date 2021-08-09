import io.github.liewhite.sql.Table

case class A(a:Int,b:String,c: String)

@main def m():Unit = {
  val a = summon[Table[A]]
  println(a.tableName)
}