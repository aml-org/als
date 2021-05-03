package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions

case object DocumentSymbolConfigType
    extends ConfigType[DocumentSymbolClientCapabilities, Either[Boolean, WorkDoneProgressOptions]]
