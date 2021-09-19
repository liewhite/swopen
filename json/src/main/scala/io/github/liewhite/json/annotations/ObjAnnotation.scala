package io.github.liewhite.json.annotations
import scala.quoted.*
import io.circe.Json

trait ObjEncodeAnnotation{
  def afterEncode(data: Json): Json
}

trait ObjDecodeAnnotation{
  def beforeDecode(data: Json): Json
}

// 传入一切可能需要的参数， 比如外层数据， 
class ObjEcho extends scala.annotation.StaticAnnotation with ObjEncodeAnnotation with ObjDecodeAnnotation {
  def afterEncode(data: Json): Json = {
    println("after encode")
    data
  }

  def beforeDecode(data: Json): Json = {
    println("after encode")
    data
  }
}

