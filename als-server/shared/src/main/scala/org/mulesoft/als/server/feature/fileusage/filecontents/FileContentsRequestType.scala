package org.mulesoft.als.server.feature.fileusage.filecontents

import org.mulesoft.lsp.feature.RequestType
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier

object FileContentsRequestType extends RequestType[TextDocumentIdentifier, FileContentsResponse]
