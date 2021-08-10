package org.mulesoft.lsp.feature.selectionRange

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions

object SelectionRangeConfigType
    extends ConfigType[SelectionRangeCapabilities, Either[Boolean, WorkDoneProgressOptions]]
