package com.liewhite.json

import org.junit.*
import io.github.liewhite.json._
import java.time.OffsetDateTime


class TestEncode:
  @Test
  def zoned = {
    val datetime = OffsetDateTime.now()
    val j = datetime.toJson
    val parsed = j.fromJsonMust[OffsetDateTime]
    assert(parsed.equals(datetime))
  }