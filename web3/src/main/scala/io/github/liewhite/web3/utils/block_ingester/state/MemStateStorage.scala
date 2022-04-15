package io.github.liewhite.web3.utils.block_ingester.state



class MemStateStorage(start: Int) extends TBlockStateStorage {
    def load(name: String) = {
        Some(BlockIngesterState(name, start))
    }
    def save(b: BlockIngesterState) = {}
}