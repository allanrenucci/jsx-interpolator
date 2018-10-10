package jsx.internal

import scala.quoted._
import scala.tasty.Tasty

import jsx.Jsx.Element
import jsx.JsxQuote

final class QuoteImpl(tasty: Tasty) {
  import tasty._

  // for debugging purpose
  private def pp(tree: Tree): Unit = {
    println(tree.show)
    println(tasty.showSourceCode.showTree(tree))
  }

  // TODO: figure out position
  private def abort(msg: String, pos: Int): Nothing =
    throw new QuoteError(msg)

  private def mkString(parts: List[String]): String = {
    val sb = new StringBuilder
    sb.append(parts.head)
    for ((part, i) <- parts.tail.zipWithIndex) {
      sb.append(Hole.encode(i))
        .append(part)
    }
    sb.toString
  }

  def expr(receiver: Expr[JsxQuote], args: Expr[Seq[Any]]): Expr[Element] = {
    import Term._

    def isStringConstant(tree: Term) = tree match {
      case Literal(_) => true // can only be a String, otherwise would not typecheck
      case _ => false
    }

    def isJsxQuoteConversion(tree: Term) =
      tree.symbol.fullName == "jsx.package$.JsxQuote"

    def isStringContextApply(tree: Term) =
      tree.symbol.fullName == "scala.StringContext$.apply"

    // jsx.JsxQuote(StringContext.apply([p0, ...]: String*)
    val parts = receiver.toTasty.underlyingArgument match {
      case Apply(conv, List(Apply(fun, List(Typed(Repeated(values), _)))))
          if isJsxQuoteConversion(conv) &&
             isStringContextApply(fun) &&
             values.forall(isStringConstant) =>
        values.collect { case Literal(Constant.String(value)) => value }
      case tree =>
        // TODO: figure out position
        pp(tree)
        abort("String literal expected", 0)
    }

    // [a0, ...]: Any*
    val Typed(Repeated(args0), _) = args.toTasty.underlyingArgument

    val elem =
      try new JsxParser(mkString(parts)).parse()
      catch {
        case JsxParser.ParseError(msg, pos) =>
          abort(s"Parsing error at pos $pos: $msg", pos)
      }


    val lifter = new Lifter {
      def liftSplice(index: Int) = {
        val splice = args0(index)
        if (splice.tpe <:< definitions.StringType)
          splice.toExpr[String]
        else
         abort(s"Type missmatch: expected String, found ${splice.tpe.show}", 0) // TODO: splice.pos
      }
    }
    lifter.liftElement(elem)
  }
}

object QuoteImpl {
  def apply(receiver: Expr[JsxQuote], args: Expr[Seq[Any]])(implicit tasty: Tasty): Expr[Element] =
    new QuoteImpl(tasty).expr(receiver, args)
}
