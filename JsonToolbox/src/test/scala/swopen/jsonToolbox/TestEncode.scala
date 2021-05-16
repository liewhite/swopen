package swopen.jsonToolbox

import org.junit.*
import swopen.jsonToolbox.JsonBehavior.{encode,decode}
import swopen.jsonToolbox.json.{Json,JsonNumber}
import swopen.jsonToolbox.modifier.Modifier

enum E:
  case A(a:Int)
  case B
  case C

case class A(a:Int)
case class B(b:Int)

case class UnionA(a:Int|String,b:String)
case class UnionB(a:Boolean,b:Double)
case class UnionC(c:Double,d:String|Boolean)


case class RecursiveC(c:Int,d:Option[RecursiveC])
class TestEncode:
  @Test
  def simpleEncode = 
    assert(1.encode == Json.JNumber(JsonNumber.JLong(1)))
    assert(1.1.encode == Json.JNumber(JsonNumber.JDouble(1.1)))
    assert("1".encode == Json.JString("1"))
    assert(true.encode == Json.JBool(true))
    assert(false.encode == Json.JBool(false))

  // big int, decimal
  @Test
  def bigNumberEncode = 
    assert(BigInt("100000000000000000000").encode == Json.JNumber(JsonNumber.JBigInt(BigInt("100000000000000000000"))))
    assert(BigDecimal("100000000000000000000.1111").encode == Json.JNumber(JsonNumber.JBigDecimal(BigDecimal("100000000000000000000.1111"))))

  // sequence 
  @Test 
  def seqEncode = 
    assert(List(1,2,3).encode == Json.deserialize("[1,2,3]").toOption.get)
    assert(Vector(1,2,3).encode == Json.deserialize("[1,2,3]").toOption.get)
    assert(Array(1,2,3).encode == Json.deserialize("[1,2,3]").toOption.get)

  // map
  @Test 
  def mapEncode = 
    case class A(key: Vector[BigInt])
    val a = Map("key" -> Vector(BigInt(1),BigInt(3),BigInt("33333333333333333333333333333333333")))
    val b = Json.deserialize("""{"key":[1,3,33333333333333333333333333333333333]}""").toOption.get
    val obj1 = a.encode.decode[A].toOption.get
    val obj2 = b.decode[A].toOption.get
    // println(obj1)
    // println(obj2)
    assert(obj1 == obj2)

  @Test 
  def renameEncode = 
    case class A(@Modifier(rename="key2") key: String, keyWithoutRename: String)
    val obj = A("rename","not rename")
    val str = obj.encode.serialize

    val newObj = Json.deserialize(str).toOption.get.decode[A].toOption.get
    assert(obj == newObj)
  
    
  @Test 
  def unionEncode = 
    val ab1: A|B = A(1)
    val ab2: A|B = B(1)
    assert(ab1.encode.serialize  == """{"a":1}""")
    assert(ab2.encode.serialize  == """{"b":1}""")
    assert(ab1.encode.decode[A|B].toOption.get  == ab1)
    assert(ab2.encode.decode[A|B].toOption.get  == ab2)
    val a : UnionA | UnionB |UnionC = UnionA(1,"asd")
    val b : UnionA | UnionB |UnionC = UnionB(true,1.2)
    val c : UnionA | UnionB |UnionC = UnionC(1.2,"c")
    assert(a.encode.decode[UnionA | UnionB |UnionC].toOption.get == a)
    assert(b.encode.decode[UnionA | UnionB |UnionC].toOption.get == b)
    assert(c.encode.decode[UnionA | UnionB |UnionC].toOption.get == c)

  @Test 
  def enumEncode = 
    val a:E = E.A(3)
    val b:E = E.B
    val c:E = E.C
    assert(a == Json.deserialize(a.encode.serialize).toOption.get.decode[E].toOption.get)
    assert(b == Json.deserialize(b.encode.serialize).toOption.get.decode[E].toOption.get)
    assert(c == Json.deserialize(c.encode.serialize).toOption.get.decode[E].toOption.get)
    
  @Test 
  def testRecursiveAdt = 
    val b = RecursiveC(1,Some(RecursiveC(1,None)))
    println(b.encode)
    // assert(a == Json.deserialize(a.encode.serialize).toOption.get.decode[RecursiveC].toOption.get)
    // assert(b == Json.deserialize(b.encode.serialize).toOption.get.decode[RecursiveC].toOption.get)