package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.lsp.feature.completion.CompletionOptions

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

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
