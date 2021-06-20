package swopen.jsonToolbox.schema

import scala.compiletime.*
import scala.quoted.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
/**
 * 
 * 默认值只影响 decode 和 json schema
 **/
trait DefaultValue[T]:
  def defaults: Map[String,JsonNode]

object DefaultValue:
  val mapper = ObjectMapper()
  inline given [T]: DefaultValue[T] = mkDefaultValue[T]

  inline def mkDefaultValue[T]: DefaultValue[T] =
    ${mkDefaultValueMacro[T]}

  def mkGiven[T](defaultMap: Map[String, Any]): DefaultValue[T] = 
    new DefaultValue:
      def defaults = defaultMap.map{case (k,v) => (k, mapper.valueToTree(v))}

  def mkDefaultValueMacro[T: Type](using Quotes): Expr[DefaultValue[T]] = 
    import quotes.reflect.*
    try
      val sym = TypeTree.of[T].symbol
      val comp = sym.companionClass
      val allNames = sym.caseFields.map(_.name)
      val names = 
        for p <- sym.caseFields if p.flags.is(Flags.HasDefault)
        yield p.name

      val body = comp.tree.asInstanceOf[ClassDef].body
      val idents: List[Ref] = 
        for case deff @ DefDef(name, _, _, _) <- body
        if name.startsWith("$lessinit$greater$default")
        yield Ref(deff.symbol)

      val namesExpr: Expr[List[String]] =
        Expr.ofList(names.map(Expr(_)))
      val identsExpr: Expr[List[Any]] =
        Expr.ofList(idents.map(_.asExpr))

      '{ mkGiven($namesExpr.zip($identsExpr).toMap) }
    catch
      case _ =>
        // report.error(s"default values typeclass only support product type: ${TypeTree.of[T].symbol}");???
        '{mkGiven(Map.empty)}