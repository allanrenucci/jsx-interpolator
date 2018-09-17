package jsx

import scala.quoted._
import scala.tasty.Tasty

object Macros {
  def quoteImpl(ctx: Expr[StringContext], args: Expr[Seq[Any]])
               (implicit tasty: Tasty): Expr[Jsx.Repr] = {
    import tasty._
    import Term._

    // for debugging purpose
    def pp(tree: Tree): Unit = {
      println(tasty.showSourceCode.showTree(tree))
    }

    def isStringConstant(tree: Term) = tree match {
      case Literal(_) => true
      case _ => false
    }

    // _root_.scala.StringContext.apply(("p0", "p1": scala.<repeated>[scala#Predef.String]))
    val parts = ctx.toTasty match {
      case Inlined(_, _,
        Apply(
          Select(Select(Select(Ident("_root_"), "scala", _), "StringContext", _), "apply", _),
          List(Typed(Repeated(values), _)))) if values.forall(isStringConstant) =>
        values.collect { case Literal(Constant.String(value)) => value }
      case _ =>
        ???
    }

    // (a0, a1: scala.<repeated>[scala.Any])
    val args0: List[Term] = args.toTasty match {
      case Inlined(_, _, Typed(Repeated(values), _)) =>
        values
      case _ =>
        ???
    }

    val string = parts.mkString("??")
    // first one for test purpose
    val arg0 = args0.head.toExpr[Any]

    '(new Jsx.Repr(~string.toExpr, ~arg0))
  }
}
