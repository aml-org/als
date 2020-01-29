package org.mulesoft.lsp.feature.common

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientVersionedTextDocumentIdentifier extends js.Object {
  def uri: String = js.native

  def version: js.UndefOr[Int] = js.native
}

object ClientVersionedTextDocumentIdentifier {
  def apply(internal: VersionedTextDocumentIdentifier): ClientVersionedTextDocumentIdentifier =
    js.Dynamic
      .literal(uri = internal.uri, version = internal.version.orUndefined)
      .asInstanceOf[ClientVersionedTextDocumentIdentifier]
}

// $COVERAGE-ON$