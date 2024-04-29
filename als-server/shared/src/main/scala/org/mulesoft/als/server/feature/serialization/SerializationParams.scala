package org.mulesoft.als.server.feature.serialization

import org.mulesoft.lsp.feature.common.TextDocumentIdentifier

case class SerializationParams(
    documentIdentifier: TextDocumentIdentifier,
    clean: Boolean = false,
    sourcemaps: Boolean = false
)
