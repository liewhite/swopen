# Json library based on circe.
* basic codec
* scala 3 derives style (skip if you're lazy, but compile time increases a lot)
* support case class fields default values
* support union type
* flatten fields in product type

# dependency
## sbt
```
libraryDependencies += "io.github.liewhite" %% "json" % "0.4.0",
```


# examples

## basic codec
```scala
import io.github.liewhite.json.JsonBehavior.{encode,decode}
import io.github.liewhite.json.codec.{Encoder,Decoder}

case class B(b:Int)  derives Encoder,Decoder // derives is optional, but compile time increase without it
case class C(c: Boolean)

assert(B(999).encode.decode[B] == Right(B(999)))
assert(C(true).encode.decode[C] == Right(C(true)))
```

## default value support
```scala
case class B(b:Int = 9)  derives Encoder,Decoder
assert(parse("{}").toOption.get.decode[B] == Right(B(3)))
```

## union type and enum
### union type
```scala
case class UnionA(a:Int|String,b:String)  derives Encoder,Decoder
case class UnionB(a:Boolean,b:Double)  derives Encoder,Decoder
case class UnionC(c:Double,d:String|Boolean)  derives Encoder,Decoder

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
```

### enum
```scala

enum D derives Encoder,Decoder{
  case D1(c: Int)
  case D2 // case object will encode to string directly
}
assert(D.D2.encode.noSpaces == """"D2"""")
assert(D.D1(1).encode.noSpaces == """{"c":1}""")
```


## flatten field
I'm always admire struct embedding in golang, but case to case inheritance is prohibited in scala. We can workaround this by flatten field in case class

```scala
import io.github.liewhite.json.typeclass.Flat

case class F(j:Boolean,k: String)  derives Encoder,Decoder
case class A(i:Int, @Flat f: F) derives Encoder,Decoder

assert(A(1,F(true,"flatten")).encode.noSpaces == """{"i":1,"j":true,"k":"flatten"}""")
```

