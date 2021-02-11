package org.mulesoft.als.actions.codeactions.plugins.declarations.samefile

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.webapi.raml.RamlTypeExtractor
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ExtractRamlTypeCodeAction(params: CodeActionRequestParams) extends ExtractSameFileDeclaration {
  override protected val kindTitle: CodeActionKindTitle = ExtractRamlTypeCodeAction

  override lazy val isApplicable: Boolean =
    homogeneousVendor && vendor.isRaml && positionIsExtracted &&
      amfObject.exists(o => ExtractorCommon.declarationPath(o, params.dialect) == Seq("types")) && appliesToDocument()

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map(
      RamlTypeExtractor
        .linkEntry(entryRange, _, entryAst, yPartBranch, amfObject, params.configuration, newName, yamlOptions))
}

object ExtractRamlTypeCodeAction extends CodeActionFactory with ExtractDeclarationKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExtractRamlTypeCodeAction(params)
}
