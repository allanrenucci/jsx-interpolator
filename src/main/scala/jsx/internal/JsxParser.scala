package jsx.internal

import jsx.Jsx._

final class JsxParser(in: String) {
  import JsxParser._

  private[this] var offset = 0
  private def next(): Unit = offset += 1
  private def isAtEnd = offset >= in.length

  private def error(msg: String, pos: Int = offset): Nothing =
    throw new ParseError(msg, pos)

  private def ch_unsafe = in.charAt(offset)

  private def ch =
    if (isAtEnd)
      error("unexpected end of input")
    else
      ch_unsafe

  private def nextChar =
    if (offset + 1 < in.length) Some(in.charAt(offset + 1))
    else None

  private def accept(expected: Char): Unit =
    if (isAtEnd)
      error(s"expected: '$expected', found end of input")
    else if (ch_unsafe != expected)
      error(s"expected: '$expected', found: '$ch_unsafe'")
    else
      next()

  private def takeWhile(pred: Char => Boolean): String = {
    val value = new StringBuilder()

    while (!isAtEnd && pred(ch_unsafe)) {
      value += ch_unsafe
      next()
    }

    value.toString
  }

  private def spaces(): Unit = takeWhile(_.isWhitespace)

  def parse(): Element = {
    spaces()
    val elem = element()
    spaces()

    if (!isAtEnd)
      error(s"expected end of input, found $ch")

    elem
  }

  private def element(): Element = {
    accept('<')
    val name = identifier()
    spaces()
    val atts = attributes()
    spaces()

    if (ch == '/') { // closing element (e.g. <div/>)
      accept('/')
      accept('>')
      return Element(name, atts, children = Nil)
    }
    accept('>')

    val cs = children()

    accept('<')
    spaces()
    accept('/')
    spaces()
    val cname = identifier()
    if (name != cname)
      error(s"names of opening element and closing element should match: $name != $cname")
    spaces()
    accept('>')

    Element(name, atts, cs)
  }

  private def isNameStart(ch: Char) = ch.isLetter

  private def identifier(): String = {
    def isNameChar(ch: Char) = isNameStart(ch) || ch == '-'

    val id = takeWhile(isNameChar)

    if (id.isEmpty)
      error("identifier expected")
    else if (!isNameStart(id.head))
      error("unexpected element name start")

    id
  }

  /** attributes ::= { attribute }
   */
  private def attributes(): List[Attribute] = {
    val atts = List.newBuilder[Attribute]

    def isAttributeStart = !isAtEnd && isNameStart(ch_unsafe)

    while (isAttributeStart) {
      atts += attribute()
      spaces()
    }

    atts.result()
  }

  /** attribute ::= identifier = attribute_value
   *
   *  attribute_value ::= hole
   *                    | "DoubleStringCharacters"
   *                    | 'SingleStringCharacters'
   */
  private def attribute(): Attribute = {
    val name = identifier()

    spaces()
    accept('=')
    spaces()

    val value = ch match {
      case '"' =>
        takeWhile(_ != '"')
      case '\'' =>
        takeWhile(_ != '\'')
      case other =>
        error(s"""expected: '"' or ''', found: '$other'""")
    }
    next()

    Attribute(name, value)
  }

  private def children(): List[Node] = {
    val cs = List.newBuilder[Node]
    var done = false

    while (!done && !isAtEnd) {
      ch_unsafe match {
        case '<' =>
          nextChar match {
            case None =>
              error("closing tag or identifier expected", pos = offset + 1)
            case Some('/') =>
              done = true
            case _ =>
              cs += element()
          }

        case c if Hole.isHoleStart(c) =>
          val hole = takeWhile(Hole.isHoleChar)
          cs += Splice(Hole.decode(hole))

        case _ =>
          val text = takeWhile(_ != '<')
          cs += Text(text)
      }
    }

    cs.result()
  }
}

object JsxParser {
  final case class ParseError(msg: String, pos: Int) extends Exception(msg)
}
