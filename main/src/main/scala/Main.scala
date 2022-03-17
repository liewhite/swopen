import io.github.liewhite.config.Configable
import io.github.liewhite.json.JsonBehavior._
import java.sql.DriverManager
import java.sql.Connection
import com.mysql.cj.jdbc.StatementImpl

@main def main = {
  // connect to the database named "mysql" on the localhost
  val driver = "com.mysql.cj.jdbc.Driver"
  val url = "jdbc:mysql://localhost/test"
  val username = "sa"
  val password = "123"

  // there's probably a better way to do this
  var connection: Connection = null

  try {
    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    // create the statement, and run the select query
    val statement = connection.prepareStatement("SELECT id,name FROM chars")
    val resultSet = statement.executeQuery()
    while (resultSet.next()) {
      val host = resultSet.getBytes("id")
      val user = resultSet.getObject("name")
      println("host, user = " + BigInt(new String(host)) + ", " + user)
    }
  } catch {
    case e => e.printStackTrace
  }
  connection.close()
}
