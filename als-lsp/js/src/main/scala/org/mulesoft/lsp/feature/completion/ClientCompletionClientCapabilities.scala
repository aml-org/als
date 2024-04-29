package org.mulesoft.lsp.feature.completion

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientCompletionClientCapabilities extends js.Object {
  def dynamicRegistration: js.UndefOr[Boolean] = js.native

  def completionItem: js.UndefOr[ClientCompletionItemClientCapabilities] =
    js.native

  def completionItemKind: js.UndefOr[ClientCompletionItemKindClientCapabilities] =
    js.native

  def contextSupport: js.UndefOr[Boolean] = js.native
}

object ClientCompletionClientCapabilities {
  def apply(internal: CompletionClientCapabilities): ClientCompletionClientCapabilities =
    js.Dynamic
      .literal(
        dynamicRegistration = internal.dynamicRegistration.orUndefined,
        completionItem = internal.completionItem.map(_.toClient).orUndefined,
        completionItemKind = internal.completionItemKind.map(_.toClient).orUndefined,
        contextSupport = internal.contextSupport.orUndefined
      )
      .asInstanceOf[ClientCompletionClientCapabilities]
}

@js.native
trait ClientCompletionItemClientCapabilities extends js.Object {
  def snippetSupport: js.UndefOr[Boolean] = js.native

  def commitCharactersSupport: js.UndefOr[Boolean] = js.native

  def deprecatedSupport: js.UndefOr[Boolean] = js.native

  def preselectSupport: js.UndefOr[Boolean] = js.native
}

object ClientCompletionItemClientCapabilities {
  def apply(internal: CompletionItemClientCapabilities): ClientCompletionItemClientCapabilities =
    js.Dynamic
      .literal(
        snippetSupport = internal.snippetSupport.orUndefined,
        commitCharactersSupport = internal.commitCharactersSupport.orUndefined,
        deprecatedSupport = internal.deprecatedSupport.orUndefined,
        preselectSupport = internal.preselectSupport.orUndefined
      )
      .asInstanceOf[ClientCompletionItemClientCapabilities]
}

@js.native
trait ClientCompletionItemKindClientCapabilities extends js.Object {
  def valueSet: js.Array[Int] = js.native
}

object ClientCompletionItemKindClientCapabilities {
  def apply(internal: CompletionItemKindClientCapabilities): ClientCompletionItemKindClientCapabilities =
    js.Dynamic
      .literal(
        valueSet = internal.valueSet.map(_.id).toJSArray
      )
      .asInstanceOf[ClientCompletionItemKindClientCapabilities]
}

// $COVERAGE-ON$
