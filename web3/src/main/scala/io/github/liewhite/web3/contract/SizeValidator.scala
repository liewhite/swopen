package io.github.liewhite.web3.contract

import scala.quoted.Expr
import scala.quoted.Quotes

object SizeValidator {

  inline def validateSize(
      inline size: Int,
      inline min: Option[Int] = None,
      inline max: Option[Int] = None,
      inline mod: Option[Int] = None,
  ) = {
    ${ impl('size, 'min, 'max, 'mod) }
  }

  def impl(
      sizeExpr: Expr[Int],
      minExpr: Expr[Option[Int]],
      maxExpr: Expr[Option[Int]],
      modExpr: Expr[Option[Int]],
  )(using quote: Quotes): Expr[Unit] = {
    import quote.reflect._

    report.info(s"size2: ${sizeExpr.value},min: ${minExpr.value}, max: ${maxExpr.value}, mod: ${modExpr.value}")

    val size = sizeExpr.value.get
    val min = minExpr.value
    val max = maxExpr.value
    val mod = modExpr.value


    if (min.isDefined && min.get.isDefined) {
      if (size < min.get.get) {
        report.error(s"size must >= ${min.get.get}: ${size}")
        return '{ ??? }
      }
    }
    if (max.isDefined && max.get.isDefined) {
      if (size > max.get.get) {
        report.error(s"size must <= ${max.get.get}: ${size}")
        return '{ ??? }
      }
    }
    if (mod.isDefined && mod.get.isDefined) {
      if (size % mod.get.get != 0) {
        report.error(s"size must mod ${mod.get.get}: ${size}")
        return '{ ??? }
      }
    }

    '{ () }

  }
}
