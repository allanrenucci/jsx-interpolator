package jsx

object Jsx {
  sealed trait Node

  case class Element(
    name: String,
    attributes: List[Attribute],
    children: List[Node]
  ) extends Node

  object Element {
    def apply(name: String, attributes: List[Attribute], children: Node*): Element =
      Element(name, attributes, children.toList)
  }

  case class Attribute(name: String, value: String)

  case class Text(value: String) extends Node

  // Compile time only.
  // Ideally, we should have two ASTs. An internal one that
  // knows about splices and a public one that doesn't.
  case class Splice(index: Int) extends Node
}
