package org.mulesoft.lsp.feature.definition

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientTextDocumentPositionParams

import scala.scalajs.js

@js.native
trait ClientDefinitionParams extends ClientTextDocumentPositionParams

object ClientDefinitionParams {
  def apply(internal: DefinitionParams): ClientDefinitionParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient, position = internal.position.toClient)
      .asInstanceOf[ClientDefinitionParams]
}
