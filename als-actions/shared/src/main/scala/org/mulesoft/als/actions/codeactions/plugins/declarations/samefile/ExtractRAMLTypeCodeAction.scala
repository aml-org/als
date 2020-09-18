package org.mulesoft.als.actions.codeactions.plugins.declarations.samefile

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.webapi.raml.RamlTypeExtractor
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ExtractRAMLTypeCodeAction(params: CodeActionRequestParams)
    extends ExtractSameFileDeclaration
    with RamlTypeExtractor {
  override protected val kindTitle: CodeActionKindTitle = ExtractRAMLTypeCodeAction

  override lazy val isApplicable: Boolean =
    vendor.isRaml && positionIsExtracted && amfObject.exists(o => declarationPath(o, params.dialect) == Seq("types"))

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map(
      linkEntry(entryRange, _, entryAst, yPartBranch, amfObject, params.configuration, newName, yamlOptions))
}

object ExtractRAMLTypeCodeAction extends CodeActionFactory with ExtractDeclarationKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExtractRAMLTypeCodeAction(params)
}
