package io.github.liewhite.web3.contract
import scala.compiletime.constValue


trait ABIPack[T] {
  // 因为要从类型上确定abi，所以必须是编译时确定静态大小
  // 包括array
  inline def staticSize: Option[Int]
}

object ABIPack {}
