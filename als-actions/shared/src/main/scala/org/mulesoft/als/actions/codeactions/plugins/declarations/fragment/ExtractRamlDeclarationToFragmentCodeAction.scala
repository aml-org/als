package org.mulesoft.als.actions.codeactions.plugins.declarations.fragment

import amf.core.model.domain.AmfObject
import amf.core.remote.Vendor
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

  // blocked by APIMF-2542 rendering changes (cannot render Custom Domain Properties)
//  override lazy val amfObject: Option[AmfObject] = {
//    val maybeObject = ExtractorCommon.amfObject(maybeTree, params.dialect)
//    if(fragmentBundleForObject(maybeObject).isDefined)
//      maybeObject // normal case
//    else {
//      val maybeParent = maybeTree.flatMap(t => t.stack.headOption).collect {
//        case d: CustomDomainProperty => d // declared annotation type
//      }
//      maybeParent orElse maybeObject
//    }
//  }

  override lazy val isApplicable: Boolean =
    params.bu.sourceVendor.contains(Vendor.RAML10) && positionIsExtracted &&
      fragmentBundle.isDefined

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override lazy val linkEntry: Future[Option[TextEdit]] =
    renderLink.map(
      RamlTypeExtractor
        .linkEntry(entryRange, _, entryAst, yPartBranch, amfObject, params.configuration, newName, yamlOptions))

  private def fragmentBundleForObject(amfObject: Option[AmfObject]): Option[FragmentBundle] =
    amfObject.flatMap(o => RamlFragmentMatcher.fragmentFor(o))

  override def fragmentBundle: Option[FragmentBundle] =
    fragmentBundleForObject(amfObject)
}

object ExtractRamlDeclarationToFragmentCodeAction extends CodeActionFactory with ExtractToFragmentKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    new ExtractRamlDeclarationToFragmentCodeAction(params)
}
