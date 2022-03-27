package io.github.liewhite.web3.contract

trait ABIPack[T] {
  def pack(t: T): Array[Byte]
}

object ABIPack {
  given ABIPack[EmptyTuple] with {
    def pack(t: EmptyTuple): Array[Byte] = Array.emptyByteArray
  }
  given [H, T <: Tuple, ABIH, ABIT <: Tuple](using
      headConverter: => ConvertFromScala[H, ABIH],
      tailConverter: => ConvertFromScala[T, ABIT]
  ): ConvertFromScala[H *: T, ABIH *: ABIT] with {
    def fromScala(value: H *: T) = {
      value match {
        case (h *: t) => {
          headConverter.fromScala(h) *: tailConverter.fromScala(t)
        }
      }
    }
  }
}