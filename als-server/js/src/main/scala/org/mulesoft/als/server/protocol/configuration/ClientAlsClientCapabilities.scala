package org.mulesoft.als.server.protocol.configuration

import org.mulesoft.als.server.protocol.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.configuration.{ClientTextDocumentClientCapabilities, ClientWorkspaceClientCapabilities}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsClientCapabilities extends js.Object {
  def workspace: js.UndefOr[ClientWorkspaceClientCapabilities] = js.native

  def textDocument: js.UndefOr[ClientTextDocumentClientCapabilities] = js.native

  def experimental: js.UndefOr[js.Object] = js.native

  def serialization: js.UndefOr[ClientSerializationClientCapabilities] = js.native

  def cleanDiagnosticTree: js.UndefOr[ClientCleanDiagnosticTreeClientCapabilities] = js.native

  def fileUsage: js.UndefOr[ClientFileUsageClientCapabilities] = js.native

  def conversion: js.UndefOr[ClientConversionClientCapabilities] = js.native
}

object ClientAlsClientCapabilities {
  def apply(internal: AlsClientCapabilities): ClientAlsClientCapabilities = {
    js.Dynamic
      .literal(
        workspace = internal.workspace.map(_.toClient).orUndefined,
        textDocument = internal.textDocument.map(_.toClient).orUndefined,
        experimental = internal.experimental.collect { case js: js.Object => js }.orUndefined,
        serialization = internal.serialization.map(_.toClient).orUndefined,
        cleanDiagnosticTree = internal.cleanDiagnosticTree.map(_.toClient).orUndefined,
        conversion = internal.conversion.map(_.toClient).orUndefined
      )
      .asInstanceOf[ClientAlsClientCapabilities]
  }
}
// $COVERAGE-ON$
