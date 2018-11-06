import jsx.internal.QuoteImpl

package object jsx {

  type Splice = String

  implicit class JsxQuote(ctx: StringContext) {
    inline def jsx(splices: => Splice*): Jsx.Element = ~QuoteImpl('(this), '(splices))
  }
}
