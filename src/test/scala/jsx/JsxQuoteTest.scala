package jsx

import org.junit.Test
import org.junit.Assert._

import Jsx._

class JsxQuoteTest {
  @Test
  def test(): Unit = {
    assertEquals(Element("a", Nil), jsx"<a/>")

    val name = "Allan"
    assertEquals(Element("a", Nil, Text(name)), jsx"<a>$name</a>")
  }
}
