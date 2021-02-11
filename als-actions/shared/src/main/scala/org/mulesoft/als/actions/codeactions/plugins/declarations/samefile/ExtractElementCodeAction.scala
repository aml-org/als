package org.mulesoft.als.actions.codeactions.plugins.declarations.samefile

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

case class ExtractElementCodeAction(params: CodeActionRequestParams) extends ExtractSameFileDeclaration {
  override protected val kindTitle: CodeActionKindTitle = ExtractElementCodeAction

  override lazy val isApplicable: Boolean =
    homogeneousVendor && !vendor.isRaml && amfObject.isDefined && positionIsExtracted &&
      params.dialect != MetaDialect.dialect && appliesToDocument()

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected def msg(params: CodeActionRequestParams): String =
    s"Extract element to declaration: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri
}

object ExtractElementCodeAction extends CodeActionFactory with ExtractDeclarationKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExtractElementCodeAction(params)
}
