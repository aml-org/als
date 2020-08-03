package org.mulesoft.lsp.feature.codeactions

import org.mulesoft.lsp.feature.common.{Range, TextDocumentIdentifier}

case class CodeActionParams(textDocument: TextDocumentIdentifier, range: Range, context: CodeActionContext)
