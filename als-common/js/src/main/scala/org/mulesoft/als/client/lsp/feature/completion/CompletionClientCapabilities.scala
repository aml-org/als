package org.mulesoft.als.client.lsp.feature.completion

import org.mulesoft.lsp.feature.completion.{
  CompletionClientCapabilities,
  CompletionItemClientCapabilities,
  CompletionItemKindClientCapabilities
}
import org.mulesoft.als.client.convert.LspConverters._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "CompletionClientCapabilities")
class ClientCompletionClientCapabilities(private val internal: CompletionClientCapabilities) {
  def dynamicRegistration: js.UndefOr[Boolean] = internal.dynamicRegistration.orUndefined
  def completionItem: js.UndefOr[ClientCompletionItemClientCapabilities] =
    internal.completionItem.map(toClientCompletionItemClientCapabilities).orUndefined
  def completionItemKind: js.UndefOr[ClientCompletionItemKindClientCapabilities] =
    internal.completionItemKind.map(toClientCompletionItemKindClientCapabilities).orUndefined
  def contextSupport: js.UndefOr[Boolean] = internal.contextSupport.orUndefined
}

@JSExportAll
@JSExportTopLevel(name = "CompletionItemClientCapabilities")
class ClientCompletionItemClientCapabilities(private val internal: CompletionItemClientCapabilities) {
  def snippetSupport: js.UndefOr[Boolean]          = internal.snippetSupport.orUndefined
  def commitCharactersSupport: js.UndefOr[Boolean] = internal.commitCharactersSupport.orUndefined
  def deprecatedSupport: js.UndefOr[Boolean]       = internal.deprecatedSupport.orUndefined
  def preselectSupport: js.UndefOr[Boolean]        = internal.preselectSupport.orUndefined
}

@JSExportAll
@JSExportTopLevel(name = "CompletionItemKindClientCapabilities")
class ClientCompletionItemKindClientCapabilities(private val internal: CompletionItemKindClientCapabilities) {
  def valueSet: js.Array[Int] = internal.valueSet.map(_.id).toJSArray
}
