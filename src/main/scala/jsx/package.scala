import jsx.internal.QuoteImpl

package object jsx {

  type Splice = String

  inline def (inline ctx: StringContext) jsx (args: Splice*): Jsx.Element =
    ~QuoteImpl(ctx, '(args))
}
