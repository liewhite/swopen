package io.github.liewhite.web3.contract.codec


// dd
trait ABIEncoder[T] {
    // 类型一定是合法的才能encode
    def encode[IN](t:T): IN
}