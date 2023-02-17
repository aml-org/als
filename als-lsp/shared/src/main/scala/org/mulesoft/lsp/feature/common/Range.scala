package org.mulesoft.lsp.feature.common

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** A range in a text document expressed as (zero-based) start and end positions. A range is comparable to a selection
  * in an editor. Therefore the end position is exclusive. If you want to specify a range that contains a line including
  * the line ending character(s) then use an end position denoting the start of the next line.
  *
  * @param start
  *   The range's start position.
  * @param end
  *   The range's end position.
  */
@JSExportAll
@JSExportTopLevel("Range")
case class Range(start: Position, end: Position)
