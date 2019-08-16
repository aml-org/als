package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.common.Range

/**
  * The document in which the command was invoked.
  */
/**
  * The range for which the command was invoked.
  */
/**
  * Context carrying additional information.
  */
case class CodeActionParams(textDocument: TextDocumentIdentifier, range: Range, context: CodeActionContext)
