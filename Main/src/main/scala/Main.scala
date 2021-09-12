import scala.deriving.*

import io.github.liewhite.json.contrib.mongodb.MongoCodec.given
import io.github.liewhite.json.codec.*
import io.github.liewhite.json.*
import io.github.liewhite.json.JsonBehavior.*
import io.github.liewhite.json.annotations.Flat
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

case class Doc(
    _id: ObjectId,
    ts_code: String,
    trade_date: String,
    adj_factor: Double
)

@main def test(): Unit = {
  // val client = MongoClients.create("mongodb://www.gaolongkeji.com:30010")
  // val db = client.getDatabase("test")
  // val coll = db.getCollection("dt")
  // val r = coll.insertOne(Document("ts_code", LocalDateTime.now()).append("trade_date",ZonedDateTime.now()))
  val doc = Document("aaa", new Date())
  val local = LocalDateTime.now()
  val zoned = ZonedDateTime.now(ZoneOffset.UTC)
  println(local.encode)
  println(zoned.encode)
  println(doc.toJson)
}
