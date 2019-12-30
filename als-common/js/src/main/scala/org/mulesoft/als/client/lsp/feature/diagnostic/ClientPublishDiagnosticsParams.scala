package org.mulesoft.als.client.lsp.feature.diagnostic

import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams

import scala.scalajs.js
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import scala.scalajs.js.JSConverters._

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
