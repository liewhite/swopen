import swopen.jsonToolbox.JsonBehavior.*

case class A(a:Int|String,b:String)
case class B(a:Boolean,b:Double)
case class C(c:Double,d:String|Boolean)
@main def test(): Unit =
  val a : A | B |C = A(1,"asd")
  val b : A | B |C = B(true,1.2)
  val c : A | B |C = C(1.2,"c")
  assert(a.encode().decode[A|B|C].toOption.get == a)
  assert(b.encode().decode[A|B|C].toOption.get == b)
  assert(c.encode().decode[A|B|C].toOption.get == c)

