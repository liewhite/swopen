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

case class Doc(
    _id: ObjectId,
    ts_code: String,
    trade_date: String,
    adj_factor: Double
)

@main def test(): Unit = {
  val client = MongoClients.create("mongodb://www.gaolongkeji.com:30010")
  val db = client.getDatabase("stock2")
  val coll = db.getCollection("adj_factor")
  val r = coll
    .find(Document("ts_code", "000001.SZ").append("trade_date", "20100202"))
    .first()
  println(r.encode.decode[Doc])
}
