package org.mulesoft.lsp.feature.completion

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCompletionContext extends js.Object {
  def triggerKind: Int                     = js.native
  def triggerCharacter: js.UndefOr[String] = js.native
}

object ClientCompletionContext {
  def apply(internal: CompletionContext): ClientCompletionContext =
    js.Dynamic
      .literal(
        triggerKind = internal.triggerKind.id,
        triggerCharacter = internal.triggerCharacter.map(_.toString).orUndefined
      )
      .asInstanceOf[ClientCompletionContext]
}

// $COVERAGE-ON$
