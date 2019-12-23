package org.mulesoft.als.client.lsp.feature.diagnostic

import org.mulesoft.lsp.feature.diagnostic.DiagnosticClientCapabilities

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("DiagnosticClientCapabilities")
class ClientDiagnosticClientCapabilities(private val internal: DiagnosticClientCapabilities) {
  def relatedInformation: js.UndefOr[Boolean] = internal.relatedInformation.orUndefined
}
