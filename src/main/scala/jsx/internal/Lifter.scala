package jsx.internal

import scala.quoted._

import jsx.Jsx._

trait Lifter {

  // Only support string splices for now
  def liftSplice(index: Int): Expr[String]

  def liftElement(elem: Element): Expr[Element] = elem.toExpr

  private implicit val nodeLiftable: Liftable[Node] = {
    case Splice(index) =>
      val value = liftSplice(index)
      '(Text(~value))
    case Text(value) =>
      '(Text(~value.toExpr))
    case elem: Element =>
      elem.toExpr
  }

  private implicit val elementLiftable: Liftable[Element] = {
    case Element(name, attributes, children) =>
      '(Element(~name.toExpr, ~attributes.toExpr, ~children.toExpr))
  }

  private implicit val attributeLiftable: Liftable[Attribute] =
    (att: Attribute) => '(Attribute(~att.name.toExpr, ~att.value.toExpr))

  private implicit def listLiftable[T : Liftable : Type]: Liftable[List[T]] = {
    case x :: xs  => '{ ~x.toExpr :: ~xs.toExpr }
    case Nil => '(Nil)
  }
}
