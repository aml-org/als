package org.mulesoft.lsp.workspace

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.{WorkspaceClientCapabilities, WorkspaceServerCapabilities}

object WorkspaceConfigType extends ConfigType[WorkspaceClientCapabilities, WorkspaceServerCapabilities]
