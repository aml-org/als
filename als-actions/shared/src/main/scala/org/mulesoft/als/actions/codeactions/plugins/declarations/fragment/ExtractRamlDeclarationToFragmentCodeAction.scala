package org.mulesoft.als.actions.codeactions.plugins.declarations.fragment

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.model.domain.extensions.CustomDomainProperty
import amf.core.internal.remote.Spec
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.webapi.raml.RamlTypeExtractor
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.webapi.raml.{
  FragmentBundle,
  RamlFragmentMatcher
}
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ExtractRamlDeclarationToFragmentCodeAction(params: CodeActionRequestParams)
    extends ExtractDeclarationToFragment {
  override protected val kindTitle: CodeActionKindTitle = ExtractRamlTypeToFragmentCodeAction

  override lazy val amfObject: Option[AmfObject] = {
    val maybeObject = extractAmfObject(maybeTree, params.documentDefinition)
    fragmentBundleForObject(maybeObject).fold { // if empty
      maybeTree.flatMap(t => t.stack.headOption).collect { case d: CustomDomainProperty =>
        d // declared annotation type
      } orElse maybeObject
    }(_ => maybeObject)
  }

  override lazy val isApplicable: Boolean =
    params.bu.sourceSpec.contains(Spec.RAML10) && positionIsExtracted &&
      fragmentBundle.isDefined

  override lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map(
      RamlTypeExtractor
        .linkEntry(entryRange, _, entryAst, yPartBranch, amfObject, params.configuration, newName, yamlOptions)
    )

  private def fragmentBundleForObject(amfObject: Option[AmfObject]): Option[FragmentBundle] =
    amfObject.flatMap(o => RamlFragmentMatcher.fragmentFor(o))

  override def fragmentBundle: Option[FragmentBundle] =
    fragmentBundleForObject(amfObject)

  override protected val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    params.findDialectForSemantic

}

object ExtractRamlDeclarationToFragmentCodeAction extends CodeActionFactory with ExtractToFragmentKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    new ExtractRamlDeclarationToFragmentCodeAction(params)
}
