package org.mulesoft.lsp.feature.hover

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientTextDocumentPositionParams

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientHoverParams extends ClientTextDocumentPositionParams {}

object ClientHoverParams {
  def apply(internal: HoverParams): ClientHoverParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient, position = internal.position.toClient)
      .asInstanceOf[ClientHoverParams]
}

// $COVERAGE-ON$
