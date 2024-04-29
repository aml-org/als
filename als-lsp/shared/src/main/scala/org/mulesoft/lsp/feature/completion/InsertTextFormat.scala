package org.mulesoft.lsp.feature.completion

/** Defines whether the insert text in a completion item should be interpreted as plain text or a snippet.
  */
case object InsertTextFormat extends Enumeration {
  type InsertTextFormat = Value

  /** The primary text to be inserted is treated as a plain string.
    */
  val PlainText: Value = Value(1)

  /** The primary text to be inserted is treated as a snippet.
    *
    * A snippet can define tab stops and placeholders with `$1`, `$2` and `${3:foo}`. `$0` defines the final tab stop,
    * it defaults to the end of the snippet. Placeholders with equal identifiers are linked, that is typing in one will
    * update others too.
    */
  val Snippet: Value = Value(2)
}
