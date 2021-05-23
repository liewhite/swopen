package swopen.jsonToolbox

import org.junit.*
import swopen.jsonToolbox.typeclasses.Annotation
import swopen.jsonToolbox.typeclasses.Annotations

class Ann(val value:String) extends scala.annotation.Annotation
class AnnExtend(value:String) extends Ann(value)

@Ann("hello")
@AnnExtend("world")
case class Target(a:Int)

case class AnnsTarget(

  @Ann("hello")
  @AnnExtend("world")
  a:Int, 

  @Ann("hello")
  @AnnExtend("world")
  b:Boolean

)
class TestAnnotation:
  @Test
  def annotation = 
    val annotations = Annotation[Ann,Target]
    assert(annotations.apply().map(_.value) == List("hello","world"))
  

  @Test
  def annotations = 
    val annotations = Annotations[Ann,AnnsTarget]
    assert(annotations().flatMap(_.map(_.value)) == List("hello","world","hello","world"))

