package org.mulesoft.als.client.lsp.feature.completion

import scala.scalajs.js

@js.native
trait ClientCompletionClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native

  def completionItem: js.UndefOr[ClientCompletionItemClientCapabilities] =
    js.native

  def completionItemKind: js.UndefOr[ClientCompletionItemKindClientCapabilities] =
    js.native

  def contextSupport: js.UndefOr[Boolean] = js.native
}

@js.native
trait ClientCompletionItemClientCapabilities extends js.Object {
  def snippetSupport: js.UndefOr[Boolean] = js.native

  def commitCharactersSupport: js.UndefOr[Boolean] = js.native

  def deprecatedSupport: js.UndefOr[Boolean] = js.native

  def preselectSupport: js.UndefOr[Boolean] = js.native
}

@js.native
trait ClientCompletionItemKindClientCapabilities extends js.Object {
  def valueSet: js.Array[Int] = js.native
}
