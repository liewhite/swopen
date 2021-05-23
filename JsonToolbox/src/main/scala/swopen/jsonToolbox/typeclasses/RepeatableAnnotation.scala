package swopen.jsonToolbox.typeclasses

import scala.deriving.*
import scala.quoted.*
import swopen.jsonToolbox.utils.*

/**
 * this file is fork from shapeless3
 **/

trait Annotation[A, T] extends Serializable {
  def apply(): List[A]
}

object Annotation {
  def apply[A,T](implicit annotations: Annotation[A, T]): Annotation[A, T] = annotations

  def mkAnnotation[A, T](annotations: List[A]): Annotation[A, T] =
    new Annotation[A, T] {
      def apply() = annotations
    }

  inline def mkAnnotation[A, T]: Annotation[A, T] = ${ AnnotationMacros.mkAnnotation }

  inline given [A, T]: Annotation[A, T] = mkAnnotation[A, T]
}

trait Annotations[A,T] extends Serializable {

  def apply(): List[List[A]]
}

object Annotations {
  def apply[A, T](implicit annotations: Annotations[A,T]): Annotations[A,T] = annotations


  def mkAnnotations[A, T](annotations: List[List[A]]): Annotations[A,T] =
    new Annotations[A, T] {
      def apply() = annotations
    }

  transparent inline implicit def mkAnnotations[A, T]: Annotations[A, T] =
    ${ AnnotationMacros.mkAnnotations[A, T] }
}

object AnnotationMacros {
  def mkAnnotation[A: Type, T: Type](using Quotes): Expr[Annotation[A, T]] = {
    import quotes.reflect._

    val annotTpe = TypeRepr.of[A]
    val annotFlags = annotTpe.typeSymbol.flags
    if (annotFlags.is(Flags.Abstract) || annotFlags.is(Flags.Trait)) {
      report.error(s"Bad annotation type ${annotTpe.show} is abstract")
      '{???}
    } else {
      val annoteeTpe = TypeRepr.of[T]
      val anns = annoteeTpe.typeSymbol.annotations.filter(_.tpe <:< annotTpe).map(_.asExprOf[A]).reverse
      val ex = if anns.isEmpty then
        Expr.ofList(List.empty[Expr[A]])
      else
        Expr.ofList(anns)
      '{ Annotation.mkAnnotation[A, T](${ex}) }
    }
  }

  def mkAnnotations[A: Type, T: Type](using q: Quotes): Expr[Annotations[A, T]] = {
    import quotes.reflect._

    val annotTpe = TypeRepr.of[A]
    val annotFlags = annotTpe.typeSymbol.flags
    if (annotFlags.is(Flags.Abstract) || annotFlags.is(Flags.Trait)) {
      report.throwError(s"Bad annotation type ${annotTpe.show} is abstract")
    } else {
      val r = new ReflectionUtils(q)
      import r._

      def mkAnnotations(annotTrees: Seq[Expr[List[A]]]): Expr[Annotations[A, T]] =
        '{ Annotations.mkAnnotations[A, T](${Expr.ofList(annotTrees)}) }

      def findAnnotation[A: Type](annoteeSym: Symbol): Expr[List[A]] =
        val anns = annoteeSym.annotations.filter(_.tpe <:< annotTpe).map(_.asExprOf[A]).reverse
        if anns.isEmpty then
          Expr.ofList(List.empty[Expr[A]])
        else
          Expr.ofList(anns)

      val annoteeTpe = TypeRepr.of[T]
      annoteeTpe.classSymbol match {
        case Some(annoteeCls) if annoteeCls.flags.is(Flags.Case) =>
          val valueParams = annoteeCls.primaryConstructor.paramSymss
            .find(_.headOption.fold(false)( _.isTerm)).getOrElse(Nil)
          val annot = mkAnnotations(valueParams.map { vparam => findAnnotation[A](vparam) })
          annot
        case Some(annoteeCls) =>
          Mirror(annoteeTpe) match {
            case Some(rm) =>
              val annot = mkAnnotations(rm.MirroredElemTypes.map { child => findAnnotation[A](child.typeSymbol) })
              annot
            case None =>
              report.throwError(s"No Annotations for sum type ${annoteeTpe.show} with no Mirror")
          }
        case None =>
          report.throwError(s"No Annotations for non-class ${annoteeTpe.show}")
      }
    }
  }
}