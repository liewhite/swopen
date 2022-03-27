package main

trait T2[A, B] {
  def convert(a:A):B
}

case class Cls1(a: Int)
case class Cls2(a: Int)

object Cls1{
  given T2[Cls1, Cls2] with {
    def convert(a:Cls1):Cls2 = Cls2(a.a)
  }
}

@main def main = {
  val c1 = Cls1(1)
  println(summon[T2[Cls1,Cls2]].convert(c1))
}
