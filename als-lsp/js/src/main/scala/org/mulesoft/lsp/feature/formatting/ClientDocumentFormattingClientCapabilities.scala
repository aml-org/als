package org.mulesoft.lsp.feature.formatting

import org.mulesoft.lsp.feature.documentFormatting.DocumentFormattingClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDocumentFormattingClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native
}

object ClientDocumentFormattingClientCapabilities {
  def apply(internal: DocumentFormattingClientCapabilities): ClientDocumentFormattingClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined
      )
      .asInstanceOf[ClientDocumentFormattingClientCapabilities]
}

// $COVERAGE-ON$
