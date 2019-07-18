package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.common.{Range, TextDocumentIdentifier}

/**
  * Context carrying additional information.
  */
case class CodeActionParams(textDocument: TextDocumentIdentifier, range: Range, context: CodeActionContext)
