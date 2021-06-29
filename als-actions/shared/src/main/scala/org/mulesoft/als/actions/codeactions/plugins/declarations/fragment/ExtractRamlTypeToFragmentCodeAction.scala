package org.mulesoft.als.actions.codeactions.plugins.declarations.fragment

import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.ExtractRamlType
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.webapi.raml.RamlTypeExtractor
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.webapi.raml.{FragmentBundle, FragmentBundles}
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ExtractRamlTypeToFragmentCodeAction(params: CodeActionRequestParams)
    extends ExtractDeclarationToFragment
    with ExtractRamlType {

  override protected val kindTitle: CodeActionKindTitle = ExtractRamlTypeToFragmentCodeAction
  override def fragmentBundle: Option[FragmentBundle]   = Some(FragmentBundles.DataTypeFragmentBundle)

  override lazy val isApplicable: Boolean =
    spec.isRaml && positionIsExtracted &&
      amfObject.exists(
        o =>
          ExtractorCommon
            .declarationPath(o, params.amfConfiguration.definitionFor(spec).getOrElse(params.dialect)) == Seq("types"))

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map(
      RamlTypeExtractor
        .linkEntry(entryRange, _, entryAst, yPartBranch, amfObject, params.configuration, newName, yamlOptions))
}

object ExtractRamlTypeToFragmentCodeAction extends CodeActionFactory with ExtractToFragmentKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    new ExtractRamlTypeToFragmentCodeAction(params)
}
