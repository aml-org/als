package org.mulesoft.lsp.feature.definition

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions

case object DefinitionConfigType
    extends ConfigType[DefinitionClientCapabilities, Either[Boolean, WorkDoneProgressOptions]]
