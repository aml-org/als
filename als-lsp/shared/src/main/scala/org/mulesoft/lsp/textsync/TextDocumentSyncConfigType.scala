package org.mulesoft.lsp.textsync

import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.textsync.TextDocumentSyncKind.TextDocumentSyncKind

case object TextDocumentSyncConfigType
    extends ConfigType[SynchronizationClientCapabilities, Either[TextDocumentSyncKind, TextDocumentSyncOptions]]
