package org.mulesoft.lsp.feature.selectionRange
import org.mulesoft.lsp.feature.common.Range

case class SelectionRange(range: Range, parent: Option[SelectionRange])