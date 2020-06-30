package org.mulesoft.lsp.feature.selectionRange

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.StaticRegistrationOptions

object SelectionRangeConfigType
    extends ConfigType[SelectionRangeCapabilities, Either[Boolean, StaticRegistrationOptions]]
