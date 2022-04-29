package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.feature.common.Range

/** An event describing a change to a text document.
  *
  * @param text
  *   The new text of the range.
  * @param range
  *   The range of the document that changed.
  * @param rangeLength
  *   The length of the range that got replaced.
  */

case class TextDocumentContentChangeEvent(text: String, range: Option[Range] = None, rangeLength: Option[Int] = None)
