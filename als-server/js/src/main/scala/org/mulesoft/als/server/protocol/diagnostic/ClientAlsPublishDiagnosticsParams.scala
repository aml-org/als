package org.mulesoft.als.server.protocol.diagnostic

import amf.ProfileName
import org.mulesoft.als.server.modules.diagnostic.AlsPublishDiagnosticsParams
import org.mulesoft.lsp.feature.diagnostic.{ClientDiagnostic, ClientPublishDiagnosticsParams}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.lsp.convert.LspConvertersSharedToClient._

// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientAlsPublishDiagnosticsParams extends js.Object {

  def uri: String = js.native

  def diagnostics: js.Array[ClientDiagnostic] = js.native

  def profile: String = js.native
}

object ClientAlsPublishDiagnosticsParams {
  def apply(internal: AlsPublishDiagnosticsParams): ClientAlsPublishDiagnosticsParams =
    js.Dynamic
      .literal(
        uri = internal.uri,
        diagnostics = internal.diagnostics.map(_.toClient).toJSArray,
        profile = internal.profile.profile
      )
      .asInstanceOf[ClientAlsPublishDiagnosticsParams]
}

// $COVERAGE-ON$
