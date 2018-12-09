package jsx.internal

import scala.quoted._
import scala.tasty.Reflection

import jsx.Jsx.Element
import jsx.Splice

final class QuoteImpl(reflect: Reflection) {
  import reflect._

  // for debugging purpose
  private def pp(tree: Tree): Unit = {
    println(tree.show)
    println(tree.showCode)
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

  def expr(ctx: StringContext, args: Expr[Seq[Splice]]): Expr[Element] = {
    import Term._

    // [a0, ...]: Any*
    val Typed(Repeated(splices), _) = args.unseal.underlyingArgument

    val elem =
      try new JsxParser(mkString(ctx.parts.toList)).parse()
      catch {
        case JsxParser.ParseError(msg, pos) =>
          abort(s"Parsing error at pos $pos: $msg", pos)
      }

    val lifter = new Lifter {
      def liftSplice(index: Int) = {
        val splice = splices(index)
        if (splice.tpe <:< definitions.StringType)
          splice.seal[String]
        else
         abort(s"Type missmatch: expected String, found ${splice.tpe.show}", splice.pos.start)
      }
    }
    lifter.liftElement(elem)
  }
}

object QuoteImpl {
  def apply(ctx: StringContext, args: Expr[Seq[Splice]])(implicit reflect: Reflection) =
    new QuoteImpl(reflect).expr(ctx, args)
}
