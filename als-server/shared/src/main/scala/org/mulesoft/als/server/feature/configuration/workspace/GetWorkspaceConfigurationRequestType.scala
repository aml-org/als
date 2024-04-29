package org.mulesoft.als.server.feature.configuration.workspace

import org.mulesoft.lsp.feature.RequestType

case object GetWorkspaceConfigurationRequestType
    extends RequestType[GetWorkspaceConfigurationParams, GetWorkspaceConfigurationResult]
