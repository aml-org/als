package org.mulesoft.als.client.lsp.common

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.common.VersionedTextDocumentIdentifier

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("VersionedTextDocumentIdentifier")
class ClientVersionedTextDocumentIdentifier(private val internal: VersionedTextDocumentIdentifier) {
  def uri: String              = internal.uri
  def version: js.UndefOr[Int] = internal.version.orUndefined
}
