package org.mulesoft.lsp.feature.completion

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCompletionOptions extends js.Object {
  def resolveProvider: js.UndefOr[Boolean]            = js.native
  def triggerCharacters: js.UndefOr[js.Array[String]] = js.native
}

object ClientCompletionOptions {
  def apply(internal: CompletionOptions): ClientCompletionOptions =
    js.Dynamic
      .literal(
        resolveProvider = internal.resolveProvider.orUndefined,
        triggerCharacters = internal.triggerCharacters.map(t => t.map(_.toString).toJSArray).orUndefined
      )
      .asInstanceOf[ClientCompletionOptions]
}

// $COVERAGE-ON$
