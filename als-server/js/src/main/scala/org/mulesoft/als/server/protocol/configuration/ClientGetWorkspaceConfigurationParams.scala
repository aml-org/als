package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.workspace.GetWorkspaceConfigurationParams
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientTextDocumentIdentifierConverter
import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientGetWorkspaceConfigurationParams extends js.Object {
  def textDocument: ClientTextDocumentIdentifier = js.native
}

object ClientGetWorkspaceConfigurationParams {
  def apply(internal: GetWorkspaceConfigurationParams): ClientGetWorkspaceConfigurationParams = {
    js.Dynamic
      .literal(
        textDocument = internal.textDocument.toClient
      )
      .asInstanceOf[ClientGetWorkspaceConfigurationParams]
  }
}
// $COVERAGE-ON$
