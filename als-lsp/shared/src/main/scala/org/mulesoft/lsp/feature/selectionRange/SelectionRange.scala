package org.mulesoft.lsp.feature.selectionRange
import org.mulesoft.lsp.feature.common.Range

case class SelectionRange(range: Range, parent: Option[SelectionRange]) {
  override def toString: String = {
    s"(${range.start.line}, ${range.start.character}) to (${range.end.line}, ${range.end.character}) [" +
      parent.map(_.toString).getOrElse("") + "]"
  }
}
