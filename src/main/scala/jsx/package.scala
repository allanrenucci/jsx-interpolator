import jsx.internal.QuoteImpl

package object jsx {
  implicit class JsxQuote(ctx: StringContext) {
    inline def jsx(args: => Any*): Jsx.Element = ~QuoteImpl('(this), '(args))
  }
}
