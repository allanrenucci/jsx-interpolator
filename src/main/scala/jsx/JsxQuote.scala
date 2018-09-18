package jsx

import internal.QuoteImpl

// Ideally should be an implicit class but the implicit conversion
// has to be a rewrite method
class JsxQuote(ctx: => StringContext) {
  rewrite def jsx(args: => Any*): Jsx.Element = ~QuoteImpl('(ctx), '(args))
}

object JsxQuote {
  implicit rewrite def JsxQuote(ctx: => StringContext): JsxQuote =
    new JsxQuote(ctx)
}
