package org.mulesoft.als.client.lsp.common

import org.mulesoft.lsp.common.Range

import scala.scalajs.js

@js.native
trait ClientLocationLink extends js.Object {
  def targetUri: String                       = js.native
  def targetRange: Range                      = js.native
  def targetSelectionRange: Range             = js.native
  def originSelectionRange: js.UndefOr[Range] = js.native
}
