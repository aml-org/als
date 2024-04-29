package org.mulesoft.lsp.feature.hover

import org.mulesoft.lsp.feature.common.Range

/** The result of a hover request.
  *
  * @contents:
  *   The hover's content
  * @range:
  *   An optional range is a range inside a text document that is used to visualize a hover, e.g. by changing the
  *   background color.
  */
case class Hover(contents: Seq[String], range: Option[Range])

object Hover {
  val empty: Hover = Hover(Nil, None)
}
