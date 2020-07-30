package org.mulesoft.lsp.feature.common

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientVersionedTextDocumentIdentifier extends js.Object {
  def uri: String = js.native

  def version: Int = js.native
}

object ClientVersionedTextDocumentIdentifier {
  def apply(internal: VersionedTextDocumentIdentifier): ClientVersionedTextDocumentIdentifier = {
    js.Dynamic
      .literal(uri = internal.uri, version = internal.version.map(_.asInstanceOf[js.Any]).orNull)
      .asInstanceOf[ClientVersionedTextDocumentIdentifier]

  }
}

// $COVERAGE-ON$
