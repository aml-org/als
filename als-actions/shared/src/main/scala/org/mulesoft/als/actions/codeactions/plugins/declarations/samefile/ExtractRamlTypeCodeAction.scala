package org.mulesoft.als.actions.codeactions.plugins.declarations.samefile

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.ExtractRamlType
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.webapi.raml.RamlTypeExtractor
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ExtractRamlTypeCodeAction(params: CodeActionRequestParams)
    extends ExtractSameFileDeclaration
    with ExtractRamlType {
  override protected val kindTitle: CodeActionKindTitle = ExtractRamlTypeCodeAction

  override lazy val isApplicable: Boolean =
    homogeneousVendor && spec.isRaml && positionIsExtracted &&
      amfObject.exists(o => ExtractorCommon.declarationPath(o, params.definedBy) == Seq("types")) && appliesToDocument()

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map(
      RamlTypeExtractor
        .linkEntry(entryRange, _, entryAst, yPartBranch, amfObject, params.configuration, newName, yamlOptions))

  override protected val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    params.findDialectForSemantic

}

object ExtractRamlTypeCodeAction extends CodeActionFactory with ExtractDeclarationKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExtractRamlTypeCodeAction(params)
}
