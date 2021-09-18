package com

import org.junit.*
import io.github.liewhite.json.JsonBehavior.{encode,decode}
import io.github.liewhite.json.codec.*
import io.github.liewhite.json.contrib.mongodb.MongoCodec.given
import io.github.liewhite.json.contrib.mongodb.*
import java.time.*
import org.bson.types.ObjectId


class TestCodec:
  @Test
  def zonedDateTime = {
    val now = ZonedDateTime.now()
    val datetime = MongoDateTime(now)
    val j = datetime.encode
    assert(j.decode[MongoDateTime].toOption.get.datetime.isEqual(now))
  }

  @Test
  def objectID = {
    val oid = ObjectId()
    val j = oid.encode
    assert(j.decode[ObjectId].toOption.get == oid)
  }

  @Test
  def oid = {
    val oid = OID(ObjectId())
    val j = oid.encode
    assert(j.decode[OID].toOption.get == oid)
  }