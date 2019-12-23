package org.mulesoft.als.client.lsp.configuration

import org.mulesoft.lsp.configuration.WorkspaceFolder

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "WorkspaceFolder")
class ClientWorkspaceFolder(private val internal: WorkspaceFolder) {
  def uri: js.UndefOr[String] = internal.uri.orUndefined

  def name: js.UndefOr[String] = internal.name.orUndefined
}
