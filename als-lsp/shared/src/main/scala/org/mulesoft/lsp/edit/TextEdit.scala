package org.mulesoft.lsp.edit

import org.mulesoft.lsp.feature.common.Range

/**
  * A textual edit applicable to a text document.
  *
  * @param range   The range of the text document to be manipulated. To insert
  *                text into a document create a range where start === end.
  * @param newText The string to be inserted. For delete operations use an
  *                empty string.
  */
case class TextEdit(range: Range, newText: String)
