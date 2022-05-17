package org.mulesoft.lsp.feature.hover

/** Describes the content type that a client supports in various result literals like `Hover`, `ParameterInfo` or
  * `CompletionItem`.
  *
  * Please note that `MarkupKinds` must not start with a `$`. This kinds are reserved for internal usage.
  */
case object MarkupKind extends Enumeration {
  type MarkupKind = Value

  /** Plain text is supported as a content format
    */
  val PlainText: MarkupKind = Value("plaintext")

  /** Markdown is supported as a content format
    */
  val Markdown: MarkupKind = Value("markdown")
}
