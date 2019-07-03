package org.mulesoft.lsp.feature.documentsymbol

import org.mulesoft.lsp.ConfigType

case object DocumentSymbolConfigType
  extends ConfigType[DocumentSymbolClientCapabilities, Unit]
