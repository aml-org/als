package org.mulesoft.lsp.feature.formatting

import org.mulesoft.lsp.feature.documentRangeFormatting.DocumentRangeFormattingClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentRangeFormattingClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
}

object ClientDocumentRangeFormattingClientCapabilities {
  def apply(internal: DocumentRangeFormattingClientCapabilities): ClientDocumentRangeFormattingClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined
      )
      .asInstanceOf[ClientDocumentRangeFormattingClientCapabilities]
}

// $COVERAGE-ON$
