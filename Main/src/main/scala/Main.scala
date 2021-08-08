import scala.deriving.*

import io.github.liewhite.json.codec.*
import io.github.liewhite.json.JsonBehavior.*
import io.github.liewhite.json.annotations.Flat
import io.github.liewhite.json.typeclass.*
import io.circe.parser._

@main def test(): Unit = {
    val a = (1,"a",true, 1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2,1.2)

    val s = a.encode
    println(s)
}

