package jsx

import org.junit.Test
import org.junit.Assert._

import Jsx._

class JsxQuoteTest {

  @Test
  def elements(): Unit = {
    assertEquals(Element("a", Nil), jsx"<a/>")
    assertEquals(Element("a", Nil), jsx"<a></a>")
    assertEquals(Element("a", Nil, Element("b", Nil)), jsx"<a><b/></a>")
  }

  @Test
  def attributes(): Unit = {
    def as(kvs: (String, String)*) = kvs.toList.map(Attribute(_, _))
    assertEquals(Element("a", as("foo" -> "bar")), jsx"<a foo='bar'/>")
    assertEquals(Element("a", as("foo" -> "bar")), jsx"""<a foo="bar"/>""")
    assertEquals(Element("a", as("foo1" -> "bar1", "foo2" -> "bar2")), jsx"<a foo1='bar1' foo2='bar2'/>")
  }

  @Test
  def splices(): Unit = {
    val name = "Allan"
    assertEquals(Element("a", Nil, Text(name)), jsx"<a>$name</a>")
  }
}
