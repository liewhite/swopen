package io.github.liewhite.http


abstract class Middleware[R<:Request]{
  def apply(req: R, next: Option[Request => Response]): Response
  //  = {
  //   val r = req.withValue("key","value")
  //   val resp = next(r)
  //   resp
  // }
}

object Middleware{
  def compose[R1 <: Request,R2 <: Request](t1: Middleware[R1], t2: Middleware[R2]): Middleware[R1] = {
    new Middleware[R1]{
      def apply(req: R1 , next: Option[Request => Response]): Response = {
        t1.apply(req, Some(req => t2.apply(req.asInstanceOf,None)))
      }
    }

  }
}

