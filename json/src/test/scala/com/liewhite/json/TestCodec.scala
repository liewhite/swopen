package com.liewhite.json

import org.junit.*
import com.liewhite.json.JsonBehavior.{encode,decode}
import com.liewhite.json.typeclass.RepeatableAnnotation
import java.math.BigInteger
import com.liewhite.json.codec.Encoder
import com.liewhite.json.codec.Decoder
import io.circe.Json
import io.circe.parser._


case class A(a:Int = 1)  
case class B(b:Int) 
case class C(c:Int) 
case class D(d:Int) 

case class UnionA(a:Int|String,b:String) 
case class UnionB(a:Boolean,b:Double) 
case class UnionC(c:Double,d:String|Boolean) 


case class RecursiveC(c:Int,d:Option[RecursiveC]) 

case class SkipNull(a: Option[Int], b: Int) 
case class DontSkipNull(a: Option[Int], b: Int) 

enum E:
  case A(a:Int)
  case B
  case C
  
enum Dft :
  case A(a:Int = 1) 
  case B(b:Int)
  case C(b:Int)
  case D

class TestEncode:
  @Test
  def simpleEncode = 
    assert(2.encode == Json.fromInt(2))
    assert(1.1.encode == Json.fromFloat(1.1).get)
    assert("1".encode == Json.fromString("1"))
    assert(true.encode == Json.True)
    assert(false.encode ==Json.False)

  // big int, decimal
  @Test
  def bigNumberEncode = 
    assert(BigInt("100000000000000000000").encode == Json.fromBigInt(BigInteger("100000000000000000000")))
    assert(BigDecimal("100000000000000000000.1111").encode == Json.fromBigDecimal(java.math.BigDecimal("100000000000000000000.1111")))

  // sequence 
  @Test 
  def seqEncode = 
    assert(List(1,2,3).encode == parse("[1,2,3]").toOption.get)
    assert(Vector(1,2,3).encode == parse("[1,2,3]").toOption.get)
    assert(Array(1,2,3).encode == parse("[1,2,3]").toOption.get)

  // map
  @Test 
  def mapEncode = 
    case class A(key: Vector[BigInt]) 
    val a = Map("key" -> Vector(BigInt(1),BigInt(3),BigInt("33333333333333333333333333333333333")))
    val b = parse("""{"key":[1,3,33333333333333333333333333333333333]}""").toOption.get
    val obj1 = a.encode.decode[A]
    val obj2 = b.decode[A]
    assert(obj1 == obj2)

  @Test 
  def unionEncode = 
    val ab1: A|B|C = A()
    val ab2: A|B|C = B(1)
    val ab3: A|B|C = C(1)
    assert(ab1.encode.noSpaces  == """{"a":1}""")
    assert(ab2.encode.noSpaces  == """{"b":1}""")
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
    assert(a == parse(a.encode.toString).toOption.get.decode[E].toOption.get)
    assert(b == parse(b.encode.toString).toOption.get.decode[E].toOption.get)
    assert(c == parse(c.encode.toString).toOption.get.decode[E].toOption.get)
    
  @Test 
  def testRecursiveAdt = 
    val a = RecursiveC(1,None)
    val b = RecursiveC(1,Some(RecursiveC(1,None)))
    assert(a == parse(a.encode.toString).toOption.get.decode[RecursiveC].toOption.get)
    assert(b == parse(b.encode.toString).toOption.get.decode[RecursiveC].toOption.get)
  @Test 
  def testEncodeDefaultValue = 
    // encode
    assert("""{"a":1}""" == A().encode.noSpaces)
    // decode
    val j = Json.fromFields(List.empty)
    val a = j.decode[A].toOption.get
    val shouldBe = A()
    assert(a == shouldBe)

  @Test 
  def testDecodeProductDefaultValue = 
    val j = Json.fromFields(List.empty)
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
