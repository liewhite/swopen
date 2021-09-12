// 外层要调用内层的process, 只能通过trait mixin实现
// 内层要访问外层的变量, 能通过继承实现
case class Request(value: String )
case class Response(value: String )

trait HttpParam[T]{
  def fromRequest(req: Request):T
}

object HttpParam{
}

trait HttpParamItem[T]{
}


case class Param(
  name: String = "xxxx",
  age: Int = 123 ,
  id: Long = 123,
)

@main def test(): Unit = {
}
