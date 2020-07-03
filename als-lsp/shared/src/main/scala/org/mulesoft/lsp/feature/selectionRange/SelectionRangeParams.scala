package org.mulesoft.lsp.feature.selectionRange

import org.mulesoft.lsp.feature.common.{Position, TextDocumentIdentifier}

case class SelectionRangeParams(textDocument: TextDocumentIdentifier, positions: Seq[Position])
