package org.mulesoft.lsp.feature.documentRangeFormatting

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions

case object DocumentRangeFormattingConfigType
    extends ConfigType[DocumentRangeFormattingClientCapabilities, Either[Boolean, WorkDoneProgressOptions]]
