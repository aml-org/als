package org.mulesoft.lsp.feature.completion

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCompletionList extends js.Object {
  def items: js.Array[ClientCompletionItem] = js.native

  def isIncomplete: Boolean = js.native
}

object ClientCompletionList {
  def apply(internal: CompletionList): ClientCompletionList =
    js.Dynamic
      .literal(
        items = internal.items.map(_.toClient).toJSArray,
        isIncomplete = internal.isIncomplete
      )
      .asInstanceOf[ClientCompletionList]
}

// $COVERAGE-ON$
