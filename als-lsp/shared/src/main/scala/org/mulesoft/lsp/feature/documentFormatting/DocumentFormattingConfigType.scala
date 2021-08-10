package org.mulesoft.lsp.feature.documentFormatting

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions

case object DocumentFormattingConfigType
    extends ConfigType[DocumentFormattingClientCapabilities, Either[Boolean, WorkDoneProgressOptions]]
