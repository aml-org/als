package org.mulesoft.als.client.lsp.common

import scala.scalajs.js

@js.native
trait ClientVersionedTextDocumentIdentifier extends js.Object {
  def uri: String              = js.native
  def version: js.UndefOr[Int] = js.native
}
