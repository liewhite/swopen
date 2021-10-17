import scala.deriving.*

import io.github.liewhite.json.codec.*
import io.github.liewhite.json.*
import io.github.liewhite.json.JsonBehavior.*
import io.github.liewhite.json.typeclass.*
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Date
import java.time.ZoneId
import java.time.ZoneOffset
import io.github.liewhite.json.annotations.*

// 每个operator有自己的IN，OUTs
// 一个endpoint包含一串operator， 然后每次添加operator时都会返回一个新的endpoint，并且对IN做intersectiontype，对OUT做union type
@SnakeCase
case class O(aNotOfI:Int)
case class Doc(
    @Flatten
    adj_factor: O
)

case class UnionA(a: Int | String, b: String) derives Encoder, Decoder
case class UnionB(a: Boolean, b: Double) derives Encoder, Decoder
case class UnionC(c: Double, d: String | Boolean) derives Encoder, Decoder

@main def test(): Unit = {
  val doc = Doc(adj_factor = O(1))
  println(doc.encode)
}
