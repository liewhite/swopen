import io.github.liewhite.sql.*
import io.getquill._

case class Person(firstName: String, lastName: String, age: Int)

// SnakeCase turns firstName -> first_name
val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
import ctx._

@main def m():Unit = {
  val t = Table[Person]
  val named = "Joe"
  inline def somePeople = {
    query[Person].filter(p => p.firstName == "joe")
  }
  val people: List[Person] = run(somePeople)
  inline def insertPeople = {
    query[Person].insert(Person("joe","xxxx", 123))
  }
  println(people)
  run(insertPeople)
}
