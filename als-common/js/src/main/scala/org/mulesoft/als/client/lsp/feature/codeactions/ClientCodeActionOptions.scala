package org.mulesoft.als.client.lsp.feature.codeactions

import scala.scalajs.js

@js.native
trait ClientCodeActionOptions extends js.Object {
  def codeActionKinds: js.UndefOr[js.Array[String]] = js.native
}
