package io.github.liewhite.web3.utils.block_ingester.state

import io.github.liewhite.sqlx.*
import io.getquill.*

case class DBState(
    ingesterId: String,
    nextBlock:  Long
)

class DBStateStorage(dbConfig: DBConfig)
    extends TBlockStateStorage {
    val ctx = getDBContext[MySQLDialect.type](dbConfig)
    import ctx._
    ctx.migrate[DBState]

    def load(name: String) = {
        val state = run(query[DBState].filter(a => a.ingesterId == lift(name)))
        if (state.isEmpty) {
            None
        } else {
            val s = state.head
            Some(BlockIngesterState(s.ingesterId, s.nextBlock))
        }
    }
    def save(b: BlockIngesterState) = {
        val exists = run(query[DBState].filter(_.ingesterId == lift(b.name)).forUpdate)
        if(!exists.isEmpty) {
            run(query[DBState].filter(_.ingesterId == lift(b.name)).update(p => p.nextBlock -> lift(b.nextBlock.longValue)) )
        }else{
            run(query[DBState].insertValue(lift(DBState(b.name,b.nextBlock.longValue))))
        }
    }
}
