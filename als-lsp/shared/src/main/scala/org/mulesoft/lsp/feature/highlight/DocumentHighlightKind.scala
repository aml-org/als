package org.mulesoft.lsp.feature.highlight

/** The kind of a document highlight.
  */
case object DocumentHighlightKind extends Enumeration {
  type DocumentHighlightKind = Value

  val Text: Value  = Value(1)
  val Read: Value  = Value(2)
  val Write: Value = Value(3)
}
