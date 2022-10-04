package org.mulesoft.als.server.modules.project

import org.mulesoft.als.server.modules.ast.WorkspaceContentListener
import org.mulesoft.amfintegration.amfconfiguration.ProjectConfigurationState

trait NewConfigurationListener extends WorkspaceContentListener[ProjectConfigurationState]
