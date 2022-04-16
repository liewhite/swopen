package io.github.liewhite.web3.utils.block_ingester.state

import io.github.liewhite.sqlx.*
import io.getquill.*

case class DbState(
    ingesterId: String,
    nextBlock:  Long
)

class DbStateStorage(dbConfig: DBConfig)
    extends TBlockStateStorage {
    val ctx = getDBContext[MySQLDialect.type](dbConfig)
    import ctx._
    ctx.migrate[DbState]

    def load(name: String) = {
        val state = run(query[DbState].filter(a => a.ingesterId == lift(name)))
        if (state.isEmpty) {
            None
        } else {
            val s = state.head
            Some(BlockIngesterState(s.ingesterId, s.nextBlock))
        }
    }
    def save(b: BlockIngesterState) = {
        val exists = run(query[DbState].filter(_.ingesterId == lift(b.name)).forUpdate)
        if(!exists.isEmpty) {
            run(query[DbState].filter(_.ingesterId == lift(b.name)).update(p => p.nextBlock -> lift(b.nextBlock.longValue)) )
        }else{
            run(query[DbState].insertValue(lift(DbState(b.name,b.nextBlock.longValue))))
        }
    }
}
