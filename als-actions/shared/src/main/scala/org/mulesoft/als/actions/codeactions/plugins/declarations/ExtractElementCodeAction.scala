package org.mulesoft.als.actions.codeactions.plugins.declarations

import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.lsp.feature.codeactions.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

case class ExtractElementCodeAction(params: CodeActionRequestParams, override val kind: CodeActionKind)
    extends ExtractSameFileDeclaration {

  override lazy val isApplicable: Boolean             = !vendor.isRaml && amfObject.isDefined && yPartBranch.exists(_.isKey)
  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected def msg(params: CodeActionRequestParams): String =
    s"Extract element to declaration: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String =
    params.uri
}

object ExtractElementCodeAction extends CodeActionFactory {
  override val kind: CodeActionKind = CodeActionKind.RefactorExtract
  override final val title          = "Extract to Declaration"
  override def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    ExtractElementCodeAction(params, kind)
}
