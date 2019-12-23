package org.mulesoft.als.client.lsp.feature.diagnostic

import org.mulesoft.lsp.common.Range
import org.mulesoft.lsp.feature.diagnostic.{Diagnostic, DiagnosticRelatedInformation}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@js.native
trait ClientDiagnostic extends js.Object {
  def range: Range                                               = js.native
  def message: String                                            = js.native
  def severity: js.UndefOr[Int]                                  = js.native
  def code: js.UndefOr[String]                                   = js.native
  def source: js.UndefOr[String]                                 = js.native
  def relatedInformation: js.Array[DiagnosticRelatedInformation] = js.native
}
