package org.mulesoft.lsp.feature.highlight

import org.mulesoft.lsp.feature.common.Range
import org.mulesoft.lsp.feature.highlight.DocumentHighlightKind.DocumentHighlightKind

case class DocumentHighlight(range: Range, kind: DocumentHighlightKind)
