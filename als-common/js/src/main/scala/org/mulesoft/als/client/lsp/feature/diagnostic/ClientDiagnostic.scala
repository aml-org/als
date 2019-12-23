package org.mulesoft.als.client.lsp.feature.diagnostic

import org.mulesoft.lsp.common.Range
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, DiagnosticRelatedInformation}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "Diagnostic")
class ClientDiagnostic(private val internal: Diagnostic) {
  def range: Range                                               = internal.range
  def message: String                                            = internal.message
  def severity: js.UndefOr[Int]                                  = internal.severity.map(_.id).orUndefined
  def code: js.UndefOr[String]                                   = internal.code.orUndefined
  def source: js.UndefOr[String]                                 = internal.source.orUndefined
  def relatedInformation: js.Array[DiagnosticRelatedInformation] = internal.relatedInformation.toJSArray
}
