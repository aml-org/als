package org.mulesoft.als.actions.codeactions.plugins.declarations.fragment

import amf.core.model.document.Fragment
import amf.plugins.document.webapi.model.DataTypeFragment
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.webapi.raml.RamlTypeExtractor
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.webapi.raml.{FragmentBundle, FragmentBundles}
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ExtractRamlTypeToFragmentCodeAction(params: CodeActionRequestParams)
    extends ExtractDeclarationToFragment
    with RamlTypeExtractor {
  override protected val kindTitle: CodeActionKindTitle = ExtractRamlTypeToFragmentCodeAction

  override lazy val isApplicable: Boolean =
    vendor.isRaml && positionIsExtracted && amfObject.exists(o => declarationPath(o, params.dialect) == Seq("types"))

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map(
      linkEntry(entryRange, _, entryAst, yPartBranch, amfObject, params.configuration, newName, yamlOptions))

  override val fragmentBundle: Option[FragmentBundle] = Some(FragmentBundles.DataTypeFragmentBundle)
}

object ExtractRamlTypeToFragmentCodeAction extends CodeActionFactory with ExtractToFragmentKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    new ExtractRamlTypeToFragmentCodeAction(params)
}
