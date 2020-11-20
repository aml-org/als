package org.mulesoft.lsp.feature.formatting

import org.mulesoft.lsp.configuration.ClientFormattingOptions
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.{
  ClientFormattingOptionsConverter,
  ClientRangeConverter,
  ClientTextDocumentIdentifierConverter
}
import org.mulesoft.lsp.feature.common.{ClientRange, ClientTextDocumentIdentifier}
import org.mulesoft.lsp.feature.documentRangeFormatting.DocumentRangeFormattingParams

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentRangeFormattingParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
  def range: ClientRange                         = js.native
  def options: ClientFormattingOptions           = js.native
}

object ClientDocumentRangeFormattingParams {
  def apply(internal: DocumentRangeFormattingParams): ClientDocumentRangeFormattingParams = {
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        range = internal.range.toClient,
        options = internal.options.toClient
      )
      .asInstanceOf[ClientDocumentRangeFormattingParams]
  }
}

// $COVERAGE-ON$ Incompatibility between scoverage and scalaJS
