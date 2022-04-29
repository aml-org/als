package org.mulesoft.lsp.feature.common

import scala.scalajs.js
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

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

// $COVERAGE-ON$
