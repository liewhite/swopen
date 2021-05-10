import swopen.jsonToolbox.JsonBehavior.*

case class A(a:Int|String,b:String)
case class B(a:Boolean,b:Double)
@main def test(): Unit =
  val a : A | B = A(1,"asd")
  val b : A | B = B(true,1.2)
  assert(a.encode().decode[A|B].toOption.get == a)
  assert(b.encode().decode[A|B].toOption.get == b)

