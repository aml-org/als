package org.mulesoft.lsp.feature.implementation

import org.mulesoft.lsp.feature.common.ClientTextDocumentPositionParams

import scala.scalajs.js
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientImplementationParams extends ClientTextDocumentPositionParams

object ClientImplementationParams {
  def apply(internal: ImplementationParams): ClientImplementationParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient, position = internal.position.toClient)
      .asInstanceOf[ClientImplementationParams]
}

// $COVERAGE-ON Incompatibility between scoverage and scalaJS
