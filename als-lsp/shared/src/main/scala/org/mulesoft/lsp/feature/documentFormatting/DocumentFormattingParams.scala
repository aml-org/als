package org.mulesoft.lsp.feature.documentFormatting

import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier

case class DocumentFormattingParams(textDocument: TextDocumentIdentifier, options: FormattingOptions)
