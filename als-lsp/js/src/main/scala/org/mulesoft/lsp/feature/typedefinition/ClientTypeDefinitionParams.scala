package org.mulesoft.lsp.feature.typedefinition

import org.mulesoft.lsp.feature.common.ClientTextDocumentPositionParams
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js

@js.native
trait ClientTypeDefinitionParams extends ClientTextDocumentPositionParams

object ClientTypeDefinitionParams {
  def apply(internal: TypeDefinitionParams): ClientTypeDefinitionParams =
    js.Dynamic
      .literal(textDocument = internal.textDocument.toClient, position = internal.position.toClient)
      .asInstanceOf[ClientTypeDefinitionParams]
}
