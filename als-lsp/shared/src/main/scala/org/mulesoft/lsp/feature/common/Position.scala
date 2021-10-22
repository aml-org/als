package org.mulesoft.lsp.feature.common

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
  * Position in a text document expressed as zero-based line and zero-based character offset. A position
  * is between two characters like an ‘insert’ cursor in a editor. Special values like for example -1 to
  * denote the end of a line are not supported.
  *
  * @param line      Line position in a document (zero-based).
  * @param character Character offset on a line in a document (zero-based). Assuming that the line is
  *                  represented as a string, the `character` value represents the gap between the
  *                  `character` and `character + 1`.
  *
  *                  If the character value is greater than the line length it defaults back to the
  *                  line length
  */
@JSExportAll
@JSExportTopLevel("lsp.Position")
case class Position(line: Int, character: Int) {
  override def toString: String = s"common.Position[L:$line,C:$character]"
}
