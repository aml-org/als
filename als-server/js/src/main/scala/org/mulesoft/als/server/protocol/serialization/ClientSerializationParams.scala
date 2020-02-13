package org.mulesoft.als.server.protocol.serialization

import org.mulesoft.als.server.feature.serialization.SerializationParams
import org.mulesoft.lsp.feature.common.ClientTextDocumentIdentifier

import scala.scalajs.js

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientSerializationParams extends js.Object {

  def documentIdentifier: ClientTextDocumentIdentifier = js.native
}

// $COVERAGE-ON$
