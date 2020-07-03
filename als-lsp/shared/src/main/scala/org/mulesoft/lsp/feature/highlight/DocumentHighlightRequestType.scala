package org.mulesoft.lsp.feature.highlight

import org.mulesoft.lsp.feature.RequestType

case object DocumentHighlightRequestType extends RequestType[DocumentHighlightParams, Seq[DocumentHighlight]]
