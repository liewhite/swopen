import io.github.liewhite.http.*

class M1 extends Middleware[Request]{
  def apply(req: Request, next: Option[Request => Response]): Response = {
    next match {
      case Some(n) => n(req.withValue("xxx","xxx"))
      case None => throw Exception("must have children")
    }
  }
}

class M2 extends Middleware[Request{val xxx:String}]{
  def apply(req: Request{val xxx:String}, next: Option[Request => Response]): Response = {
    Response(200,Map.empty,req.xxx.getBytes)
  }
}

@main def test(): Unit = {
  val m = Middleware.compose(new M1(),new M2())
  println(new String(m(Request(),None).body))
}

