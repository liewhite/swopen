package io.github.liewhite.http

import scala.quoted.Quotes
import scala.quoted.Type
import scala.quoted.Expr

/**
 *  req -> middleware1 -> middleware2 ---->
 *                                        |
 *                                        middleware3, called handler here
 *                                        |
 *                                        |
 *response <- middleware1 <- middleware2<--          
 * 内层的middleware 约束外层类型
 * 
 * */



case class Response(statusCode: Int, headers: Map[String, String], body: Array[Byte])

case class Request(values: Map[String,Any] = Map.empty) extends Selectable{
  def selectDynamic(name: String): Any = {
    values(name)
  }
}

object Request{
  extension[R <: Request] (t:R){
    transparent inline def withValue[T](inline key: String,value: T) = {
      ${Request.withValueMacro[R,T]('t, 'key, 'value)}
    }
  }
  
  def withValueMacro[R<: Request : Type, T:Type](request: Expr[R], key: Expr[String], value: Expr[T])(using q: Quotes) = {
    import q.reflect.*
    val requestType = TypeRepr.of[R]
    val newReqType = Refinement(requestType, key.value.get, TypeRepr.of[T])
    newReqType.asType match {
      case '[t] => {
        '{
          ${request}.copy(values = ${request}.values.updated(${key}, ${value})).asInstanceOf[t]
        }
      }
    }
  }
}