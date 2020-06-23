package org.mulesoft.lsp.feature.hover

import org.mulesoft.lsp.feature.common.ClientRange
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS
@js.native
trait ClientHover extends js.Object {
  def contents: js.Array[String]     = js.native
  def range: js.UndefOr[ClientRange] = js.native
}

object ClientHover {
  def apply(internal: Hover): ClientHover =
    js.Dynamic
      .literal(
        contents = internal.contents.toJSArray,
        range = internal.range.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientHover]
}
// $COVERAGE-ON$
