package org.mulesoft.als.client.lsp.common

import org.mulesoft.lsp.common.TextDocumentItem

import scala.scalajs.js

@js.native
trait ClientTextDocumentItem extends js.Object {
  def uri: String = js.native

  def languageId: String = js.native

  def version: Int = js.native

  def text: String = js.native
}

object ClientTextDocumentItem {
  def apply(internal: TextDocumentItem): ClientTextDocumentItem =
    js.Dynamic
      .literal(uri = internal.uri, languageId = internal.languageId, version = internal.version, text = internal.text)
      .asInstanceOf[ClientTextDocumentItem]
}
