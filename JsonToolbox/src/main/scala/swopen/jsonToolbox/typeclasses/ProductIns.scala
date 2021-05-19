package swopen.jsonToolbox.typeclasses

import scala.deriving.*
import swopen.jsonToolbox.utils.SummonUtils.*

class ArrayProduct(val elems: Array[Any]) extends Product {
    def canEqual(that: Any): Boolean = true
    def productElement(n: Int) = elems(n)
    def productArity = elems.length
    override def productIterator: Iterator[Any] = elems.iterator
  }

trait ProductInst[T[_],A]:
  def elemT: List[T[Any]]
  def fromProduct(data:Product):A

object ProductInst:
  inline given [T[_],A](using mirror: Mirror.ProductOf[A]): ProductInst[T,A] = new ProductInst[T,A]{
    def elemT = summonAll[T, mirror.MirroredElemTypes]

    def fromProduct(data: Product):A = mirror.fromProduct(data)

  }