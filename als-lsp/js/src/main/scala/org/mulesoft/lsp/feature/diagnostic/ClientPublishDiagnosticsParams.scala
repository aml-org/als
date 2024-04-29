package org.mulesoft.lsp.feature.diagnostic

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientPublishDiagnosticsParams extends js.Object {

  def uri: String = js.native

  def diagnostics: js.Array[ClientDiagnostic] = js.native
}

object ClientPublishDiagnosticsParams {
  def apply(internal: PublishDiagnosticsParams): ClientPublishDiagnosticsParams =
    js.Dynamic
      .literal(
        uri = internal.uri,
        diagnostics = internal.diagnostics.map(_.toClient).toJSArray
      )
      .asInstanceOf[ClientPublishDiagnosticsParams]
}

// $COVERAGE-ON$
