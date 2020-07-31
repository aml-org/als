package org.mulesoft.als.server.feature.renameFile

import org.mulesoft.lsp.feature.common.TextDocumentIdentifier

case class RenameFileActionParams(oldDocument: TextDocumentIdentifier, newDocument: TextDocumentIdentifier)
