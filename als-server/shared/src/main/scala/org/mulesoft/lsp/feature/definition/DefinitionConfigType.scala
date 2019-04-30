package org.mulesoft.lsp.feature.definition

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind
import org.mulesoft.lsp.textsync.TextDocumentSyncOptions

case object DefinitionConfigType
  extends ConfigType[DefinitionClientCapabilities, Unit]
