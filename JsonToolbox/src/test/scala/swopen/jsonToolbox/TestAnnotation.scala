package swopen.jsonToolbox

import org.junit.*
import swopen.jsonToolbox.typeclasses.RepeatableAnnotation
import swopen.jsonToolbox.typeclasses.RepeatableAnnotations
import swopen.jsonToolbox.codec.*

class Ann(val value:String) extends scala.annotation.Annotation
class AnnExtend(value:String) extends Ann(value)

@Ann("hello")
@AnnExtend("world")
case class Target(a:Int) derives Encoder,Decoder


case class AnnsTarget(

  @Ann("hello")
  @AnnExtend("world a")
  a:Int, 

  @Ann("hello")
  @AnnExtend("world b")
  b:Boolean

) derives Encoder,Decoder

class TestAnnotation:
  @Test
  def annotation = 
    val annotations = RepeatableAnnotation[Ann,Target]
    assert(annotations.apply().map(_.value) == List("hello","world"))
  

  @Test
  def annotations = 
    val annotations = RepeatableAnnotations[Ann,AnnsTarget]
    assert(annotations().flatMap(_.map(_.value)) == List("hello","world a","hello","world b"))

