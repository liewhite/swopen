package main
import java.sql.*
import org.jooq.*;
import org.jooq.impl.*;
import scala.jdk.CollectionConverters.*

@main def main = {
  // connect to the database named "mysql" on the localhost
  val url = "jdbc:mysql://localhost/test"
  val username = "sa"
  val password = "123"

  // // make the connection
  val connection = DriverManager.getConnection(url, username, password)
  val create = DSL.using(connection, SQLDialect.MYSQL)
  create.meta.getTables("un1").asScala.foreach(table => {
    table.fields.foreach(field => {
      println((field.getName,field.getDataType.getCastTypeName))
    })
  })
}
