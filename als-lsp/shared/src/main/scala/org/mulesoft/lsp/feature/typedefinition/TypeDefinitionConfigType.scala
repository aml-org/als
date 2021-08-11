package org.mulesoft.lsp.feature.typedefinition

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions

case object TypeDefinitionConfigType
    extends ConfigType[TypeDefinitionClientCapabilities, Either[Boolean, WorkDoneProgressOptions]]
