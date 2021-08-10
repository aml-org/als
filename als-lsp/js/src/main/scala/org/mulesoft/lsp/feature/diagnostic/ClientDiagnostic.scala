package org.mulesoft.lsp.feature.diagnostic

import org.mulesoft.lsp.convert.LspConvertersSharedToClient._
import org.mulesoft.lsp.feature.common.ClientRange

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDiagnostic extends js.Object {
  def range: ClientRange                                               = js.native
  def message: String                                                  = js.native
  def severity: js.UndefOr[Int]                                        = js.native
  def code: js.UndefOr[String]                                         = js.native
  def codeDescription: js.UndefOr[ClientDiagnosticCodeDescription]     = js.native
  def source: js.UndefOr[String]                                       = js.native
  def relatedInformation: js.Array[ClientDiagnosticRelatedInformation] = js.native
}

object ClientDiagnostic {
  def apply(internal: Diagnostic): ClientDiagnostic =
    js.Dynamic
      .literal(
        range = internal.range.toClient,
        message = internal.message,
        severity = internal.severity.map(_.id).orUndefined,
        code = internal.code.orUndefined,
        codeDescription = internal.codeDescription.map(ClientDiagnosticCodeDescription(_)).orUndefined,
        source = internal.source.orUndefined,
        relatedInformation = internal.relatedInformation.map(_.toClient).toJSArray
      )
      .asInstanceOf[ClientDiagnostic]
}

// $COVERAGE-ON$