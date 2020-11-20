package org.mulesoft.lsp.feature.formatting

import org.mulesoft.lsp.configuration.ClientFormattingOptions
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.{
  ClientFormattingOptionsConverter,
  ClientTextDocumentIdentifierConverter
}
import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier
import org.mulesoft.lsp.feature.documentFormatting.DocumentFormattingParams

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentFormattingParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
  def options: ClientFormattingOptions           = js.native
}

object ClientDocumentFormattingParams {
  def apply(internal: DocumentFormattingParams): ClientDocumentFormattingParams = {
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient,
        options = internal.options.toClient
      )
      .asInstanceOf[ClientDocumentFormattingParams]
  }
}

// $COVERAGE-ON$
