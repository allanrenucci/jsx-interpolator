package jsx.internal

import scala.quoted._
import scala.tasty.Tasty

import jsx.Jsx.Element

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

  def expr(sc: Expr[StringContext], args: Expr[Seq[Any]]): Expr[Element] = {
    import Term._

    def isStringConstant(tree: Term) = tree match {
      case Literal(_) => true // can only be a String, otherwise would not typecheck
      case _ => false
    }

    // _root_.scala.StringContext.apply([p0, ...]: String*)
    val parts = sc.toTasty match {
      case Inlined(_, _,
        Apply(
          Select(Select(Select(Ident("_root_"), "scala", _), "StringContext", _), "apply", _),
          List(Typed(Repeated(values), _)))) if values.forall(isStringConstant) =>
        values.collect { case Literal(Constant.String(value)) => value }
      case tree =>
        // TODO: figure out position
        pp(tree)
        abort("String literal expected", 0)
    }

    val elem =
      try new JsxParser(mkString(parts)).parse()
      catch {
        case JsxParser.ParseError(msg, pos) =>
          abort(s"Parsing error at pos $pos: $msg", pos)
      }

    // [a0, ...]: Any*
    val Inlined(_, _, Typed(Repeated(args0), _)) = args.toTasty
    val lifter = new Lifter {
      def liftSplice(index: Int) = {
        val splice = args0(index)
        if (splice.tpe <:< definitions.AnyType) // TODO: should be definitions.StringType
          splice.toExpr[String]
        else
         abort(s"Type missmatch: expected String, found ${splice.tpe.show}", 0) // TODO: splice.pos
      }
    }
    lifter.liftElement(elem)
  }
}

object QuoteImpl {
  def apply(sc: Expr[StringContext], args: Expr[Seq[Any]])(implicit tasty: Tasty): Expr[Element] =
    new QuoteImpl(tasty).expr(sc, args)
}
