package org.mulesoft.als.client.lsp.common

import org.mulesoft.als.client.convert.LspConverters._
import org.mulesoft.lsp.common.LocationLink
import org.mulesoft.lsp.common.Range

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("LocationLink")
class ClientLocationLink(private val internal: LocationLink) {
  def targetUri: String                       = internal.targetUri
  def targetRange: Range                      = internal.targetRange
  def targetSelectionRange: Range             = internal.targetSelectionRange
  def originSelectionRange: js.UndefOr[Range] = internal.originSelectionRange.orUndefined
}
