package io.github.liewhite.web3.utils.block_ingester.state

case class BlockIngesterState(
    name:      String,
    nextBlock: BigInt
)

trait TBlockStateStorage {
    def load(name: String): Option[BlockIngesterState]
    def save(t: BlockIngesterState): Unit
}
