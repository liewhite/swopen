package swopen.jsonToolbox

import org.junit.*
import swopen.jsonToolbox.JsonBehavior.{encode,decode}
import swopen.jsonToolbox.modifier.Modifier
import swopen.jsonToolbox.typeclasses.RepeatableAnnotation
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.FloatNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.BigIntegerNode
import java.math.BigInteger
import com.fasterxml.jackson.databind.node.DecimalNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.DoubleNode
import swopen.jsonToolbox.codec.Encoder
import swopen.jsonToolbox.codec.Decoder
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory


case class A(a:Int = 1)  derives Encoder,Decoder
case class B(b:Int) derives Encoder,Decoder
case class C(c:Int) derives Encoder,Decoder
case class D(d:Int) derives Encoder,Decoder

case class UnionA(a:Int|String,b:String) derives Encoder,Decoder
case class UnionB(a:Boolean,b:Double) derives Encoder,Decoder
case class UnionC(c:Double,d:String|Boolean) derives Encoder,Decoder


case class RecursiveC(c:Int,d:Option[RecursiveC]) derives Encoder,Decoder

case class SkipNull(a: Option[Int], b: Int) derives Encoder,Decoder
case class DontSkipNull(a: Option[Int], b: Int) derives Encoder,Decoder

enum E:
  case A(a:Int)
  case B
  case C
enum Dft derives Encoder,Decoder:
  case A(a:Int = 1) 
  case B(b:Int)
  case C(b:Int)
  case D

class TestEncode:
  val mapper = ObjectMapper()

  @Test
  def simpleEncode = 
    assert(2.encode == IntNode(2))
    assert(1.1.encode == DoubleNode(1.1))
    assert("1".encode == TextNode("1"))
    assert(true.encode == BooleanNode.TRUE)
    assert(false.encode ==BooleanNode.FALSE)

  // big int, decimal
  @Test
  def bigNumberEncode = 
    assert(BigInt("100000000000000000000").encode == BigIntegerNode.valueOf(BigInteger("100000000000000000000")))
    assert(BigDecimal("100000000000000000000.1111").encode == DecimalNode(java.math.BigDecimal("100000000000000000000.1111")))

  // sequence 
  @Test 
  def seqEncode = 
    assert(List(1,2,3).encode == mapper.readTree("[1,2,3]"))
    assert(Vector(1,2,3).encode == mapper.readTree("[1,2,3]"))
    assert(Array(1,2,3).encode == mapper.readTree("[1,2,3]"))

  // map
  @Test 
  def mapEncode = 
    case class A(key: Vector[BigInt]) derives Encoder,Decoder
    val a = Map("key" -> Vector(BigInt(1),BigInt(3),BigInt("33333333333333333333333333333333333")))
    val b = mapper.readTree("""{"key":[1,3,33333333333333333333333333333333333]}""")
    val obj1 = a.encode.decode[A]
    val obj2 = b.decode[A]
    assert(obj1 == obj2)

  @Test 
  def unionEncode = 
    val ab1: A|B|C = A()
    val ab2: A|B|C = B(1)
    val ab3: A|B|C = C(1)
    assert(ab1.encode.toString  == """{"a":1}""")
    assert(ab2.encode.toString  == """{"b":1}""")
    assert(ab1.encode.decode[A|B|C].toOption.get  == ab1)
    assert(ab2.encode.decode[A|B|C].toOption.get  == ab2)
    assert(ab3.encode.decode[A|B|C].toOption.get  == ab3)
    val a : UnionA | UnionB |UnionC = UnionA(1,"asd")
    val b : UnionA | UnionB |UnionC = UnionB(true,1.2)
    val c : UnionA | UnionB |UnionC = UnionC(1.2,"c")
    assert(a.encode.decode[UnionA | UnionB |UnionC].toOption.get == a)
    assert(b.encode.decode[UnionA | UnionB |UnionC].toOption.get == b)
    assert(c.encode.decode[UnionA | UnionB |UnionC].toOption.get == c)

  @Test 
  def enumEncode = ()
    val a:E = E.A(3)
    val b:E = E.B
    val c:E = E.C
    assert(a == mapper.readTree(a.encode.toString).decode[E].toOption.get)
    assert(b == mapper.readTree(b.encode.toString).decode[E].toOption.get)
    assert(c == mapper.readTree(c.encode.toString).decode[E].toOption.get)
    
  @Test 
  def testRecursiveAdt = 
    val a = RecursiveC(1,None)
    val b = RecursiveC(1,Some(RecursiveC(1,None)))
    assert(a == mapper.readTree(a.encode.toString).decode[RecursiveC].toOption.get)
    assert(b == mapper.readTree(b.encode.toString).decode[RecursiveC].toOption.get)
  @Test 
  def testEncodeDefaultValue = 
    // encode
    assert("""{"a":1}""" == A().encode.toString)
    // decode
    val j = ObjectNode(JsonNodeFactory.instance)
    val a = j.decode[A].toOption.get
    val shouldBe = A()
    assert(a == shouldBe)

  @Test 
  def testDecodeProductDefaultValue = 
    val j = ObjectNode(JsonNodeFactory.instance)
    val a = j.decode[A].toOption.get
    val shouldBe = A()
    assert(a == shouldBe)

  @Test 
  def testDecodeCoproductDefaultValue = 
    val a = Dft.B(1)
    val b = Dft.B(2)
    val c = Dft.B(3)
    val d = Dft.D
    assert(a == a.encode.decode[Dft].toOption.get)
    assert(b == b.encode.decode[Dft].toOption.get)
    assert(c == c.encode.decode[Dft].toOption.get)
    assert(d == d.encode.decode[Dft].toOption.get)
