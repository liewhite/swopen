package io.github.liewhite.json

import java.time.ZonedDateTime
import upickle.core.Visitor
import java.time.format.DateTimeFormatter
import java.time.Instant

trait TSwopenJson extends upickle.AttributeTagged {
    given Writer[ZonedDateTime] with {
        def write0[V](
            out: Visitor[_, V],
            v: ZonedDateTime
        ): V = {
            out.visitString(v.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), -1)
        }
    }
    given Reader[ZonedDateTime] = {
        new Reader.Delegate[Any, ZonedDateTime](summon[Reader[String]].map(s => {
            ZonedDateTime.parse(s)
        }))
    }

    extension [T: ReadWriter](t: T) {
        def toJsonStr(
            indent: Option[Int] = None,
            escapeUnicode: Boolean = false
        ): String = {
            write(t)
        }
    }
}
object SwopenJson extends TSwopenJson {
    override def serializeDefaults = true
}
