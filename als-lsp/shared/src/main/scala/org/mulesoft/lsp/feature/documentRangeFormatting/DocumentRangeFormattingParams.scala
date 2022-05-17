package org.mulesoft.lsp.feature.documentRangeFormatting

import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.feature.common.{Range, TextDocumentIdentifier}

case class DocumentRangeFormattingParams(textDocument: TextDocumentIdentifier, range: Range, options: FormattingOptions)
