package swopen.jsonToolbox.typeclasses

import scala.deriving.*
import scala.quoted.*
import swopen.jsonToolbox.utils.*

/**
 * this file is fork from shapeless3
 **/

trait RepeatableAnnotation[A, T] extends Serializable {
  def apply(): List[A]
}

object RepeatableAnnotation {
  def apply[A,T](implicit annotations: RepeatableAnnotation[A, T]): RepeatableAnnotation[A, T] = annotations

  def mkAnnotation[A, T](annotations: List[A]): RepeatableAnnotation[A, T] =
    new RepeatableAnnotation[A, T] {
      def apply() = annotations
    }

  inline def mkAnnotation[A, T]: RepeatableAnnotation[A, T] = ${ AnnotationMacros.mkAnnotation }

  inline given [A, T]: RepeatableAnnotation[A, T] = mkAnnotation[A, T]
}

trait RepeatableAnnotations[A,T] extends Serializable {

  def apply(): List[List[A]]
}

object RepeatableAnnotations {
  def apply[A, T](implicit annotations: RepeatableAnnotations[A,T]): RepeatableAnnotations[A,T] = annotations


  def mkAnnotations[A, T](annotations: List[List[A]]): RepeatableAnnotations[A,T] =
    new RepeatableAnnotations[A, T] {
      def apply() = annotations
    }

  transparent inline implicit def mkAnnotations[A, T]: RepeatableAnnotations[A, T] =
    ${ AnnotationMacros.mkAnnotations[A, T] }
}

object AnnotationMacros {
  def mkAnnotation[A: Type, T: Type](using Quotes): Expr[RepeatableAnnotation[A, T]] = {
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
      '{ RepeatableAnnotation.mkAnnotation[A, T](${ex}) }
    }
  }

  def mkAnnotations[A: Type, T: Type](using q: Quotes): Expr[RepeatableAnnotations[A, T]] = {
    import quotes.reflect._

    val annotTpe = TypeRepr.of[A]
    val annotFlags = annotTpe.typeSymbol.flags
    if (annotFlags.is(Flags.Abstract) || annotFlags.is(Flags.Trait)) {
      report.throwError(s"Bad annotation type ${annotTpe.show} is abstract")
    } else {
      val r = new ReflectionUtils(q)
      import r._

      def mkAnnotations(annotTrees: Seq[Expr[List[A]]]): Expr[RepeatableAnnotations[A, T]] =
        '{ RepeatableAnnotations.mkAnnotations[A, T](${Expr.ofList(annotTrees)}) }

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