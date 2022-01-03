import scala.collection.mutable
class RequestContext

trait Operator[IN,OUT]{
    def output(ctx: RequestContext):OUT
}

case class Router(parent: Option[Router], operators: Vector[Operator[_,_]], children: mutable.ListBuffer[Router]){
    def register(routers: Router*) = {
        
    }
}

object Router{
    def newRouter(operators: Vector[Operator[_,_]]): Router = {
        Router(None,operators, mutable.ListBuffer.empty)
    }
}