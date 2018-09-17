package jsx

import org.junit.Test
import org.junit.Assert._

import JsxQuote._

class JsxQuoteTest {
  @Test
  def test(): Unit = {
    val name = new Object{}
    assertEquals(Jsx.Repr("Hello ??!", name), jsx"Hello $name!")
  }
}
