package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.feature.configuration.workspace.GetWorkspaceConfigurationResult
import org.mulesoft.lsp.convert.LspConvertersSharedToClient.ClientDidChangeConfigurationNotificationParamsConverter
import org.mulesoft.lsp.textsync.ClientDidChangeConfigurationNotificationParams

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientGetWorkspaceConfigurationResult extends js.Object {
  def workspace: String = js.native
  def configuration: ClientDidChangeConfigurationNotificationParams
}

object ClientGetWorkspaceConfigurationResult {
  def apply(internal: GetWorkspaceConfigurationResult): ClientGetWorkspaceConfigurationResult = {
    js.Dynamic
      .literal(
        workspace = internal.workspace,
        configuration = internal.configuration.toClient
      )
      .asInstanceOf[ClientGetWorkspaceConfigurationResult]
  }
}
// $COVERAGE-ON$
