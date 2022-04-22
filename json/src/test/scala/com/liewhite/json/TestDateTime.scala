package com.liewhite.json

import org.junit.*
import io.github.liewhite.json.SwopenJson.*
import java.time.ZonedDateTime


class TestEncode:
  @Test
  def zoned = {
    val datetime = ZonedDateTime.now()
    val j = datetime.toJsonStr()
    val parsed = read[ZonedDateTime](j)
    assert(parsed.isEqual(datetime))
  }