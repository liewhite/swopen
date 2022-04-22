package io.github.liewhite.config

import io.github.liewhite.json.SwopenJson.*
import io.circe.Json
import upickle.core.Visitor

class Secret(val s: String) {
    override def toString: String = "******"
}

object Secret {
    given Writer[Secret] with {
        def write0[V](
            out: Visitor[_, V],
            v: Secret
        ): V = {
            out.visitString(v.s,-1)
        }
    }
    given Reader[Secret] = {
        new Reader.Delegate[Any, Secret](summon[Reader[String]].map(s => {
            Secret(s)
        }))
    }
}
