package org.mulesoft.lsp.feature.completion

import org.mulesoft.lsp.common.{Position, TextDocumentIdentifier, TextDocumentPositionParams}


case class CompletionParams(textDocument: TextDocumentIdentifier,
                            position: Position,
                            context: Option[CompletionContext] = None)
  extends TextDocumentPositionParams
