package org.mulesoft.als.actions.codeactions.plugins.declarations.`trait`

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import amf.apicontract.client.scala.model.domain.Operation
import amf.apicontract.client.scala.model.domain.templates.ParametrizedTrait
import amf.apicontract.internal.metamodel.domain.OperationModel
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.parser.domain.Annotations
import amf.core.internal.remote.Spec
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.{ConverterExtractor, ExtractorCommon}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

class ExtractTraitCodeAction(override protected val params: CodeActionRequestParams)
    extends ConverterExtractor[Operation, Operation] {
  override protected val kindTitle: CodeActionKindTitle = ExtractTraitCodeAction

  override val declarationKey: String = "traits"

  override lazy val original: Option[Operation] = amfObject match {
    case Some(e: Operation) => Some(e)
    case _                  => None
  }

  override protected def newName: String = ExtractorCommon.nameNotInList("trait", params.bu.declaredNames.toSet)

  override val isApplicable: Boolean =
    params.bu.sourceSpec.contains(Spec.RAML10) && original.isDefined

  override def transform(original: Operation): Operation =
    Operation(original.fields, Annotations())

  override def modifyEntry(original: Operation): String = {

    val fields = original.fields
      .copy()
      .filter({
        case (field, _) if field == OperationModel.Method      => true
        case (field, _) if field == OperationModel.OperationId => true // necessary?
        case _                                                 => false
      })

    val result = original.copy(fields, Annotations())

    val newExtends: Seq[DomainElement] = Seq(ParametrizedTrait().withName(newName))
    result.withExtends(newExtends)

    val node = params.alsConfigurationState.configForDefinition(params.documentDefinition).emit(result)
    s"\n${renderNode(node, yPartBranch.flatMap(_.closestEntry))}\n"
  }

  override protected def msg(params: CodeActionRequestParams): String =
    s"Extract trait: \n\t${params.uri}\t${params.range}"

  override protected val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    params.findDialectForSemantic
}

object ExtractTraitCodeAction extends CodeActionFactory with ExtractTraitKind {
  def apply(params: CodeActionRequestParams): CodeActionResponsePlugin = new ExtractTraitCodeAction(params)
}
