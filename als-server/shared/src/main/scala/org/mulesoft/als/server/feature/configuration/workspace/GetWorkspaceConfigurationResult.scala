package org.mulesoft.als.server.feature.configuration.workspace

import org.mulesoft.lsp.textsync.DidChangeConfigurationNotificationParams

case class GetWorkspaceConfigurationResult(workspace: String, configuration: DidChangeConfigurationNotificationParams)
