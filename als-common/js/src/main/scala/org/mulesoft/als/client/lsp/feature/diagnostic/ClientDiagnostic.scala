package org.mulesoft.als.client.lsp.feature.diagnostic

import org.mulesoft.als.client.lsp.common.ClientRange
import org.mulesoft.lsp.feature.diagnostic.Diagnostic

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

@js.native
trait ClientDiagnostic extends js.Object {
  def range: ClientRange                                               = js.native
  def message: String                                                  = js.native
  def severity: js.UndefOr[Int]                                        = js.native
  def code: js.UndefOr[String]                                         = js.native
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
        source = internal.source.orUndefined,
        relatedInformation = internal.relatedInformation.map(_.toClient).toJSArray
      )
      .asInstanceOf[ClientDiagnostic]
}

// $COVERAGE-ON$