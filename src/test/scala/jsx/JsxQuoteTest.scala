package jsx

import org.junit.Test
import org.junit.Assert._

import JsxQuote._
import Jsx._

class JsxQuoteTest {
  @Test
  def test(): Unit = {
    // assertEquals(Element("a", Nil), jsx"<a/>") // FIXME
    val name = "Allan"
    assertEquals(Element("a", Nil, Text(name)), jsx"<a>$name</a>")
  }
}
