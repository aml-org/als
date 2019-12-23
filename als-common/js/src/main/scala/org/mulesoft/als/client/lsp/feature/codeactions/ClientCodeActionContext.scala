package org.mulesoft.als.client.lsp.feature.codeactions

import org.mulesoft.als.client.lsp.feature.diagnostic.ClientDiagnostic

import scala.scalajs.js

@js.native
trait ClientCodeActionContext extends js.Object {
  def diagnostics: js.Array[ClientDiagnostic] = js.native
  def only: js.UndefOr[js.Array[String]]      = js.native
}
