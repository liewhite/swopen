package io.github.liewhite.sql

import scala.collection.concurrent
import org.jooq

object Tables {
    val jooqTablesCache = concurrent.TrieMap.empty[String, jooq.Table[_]]
}