package org.mulesoft.als.actions.codeactions.plugins.declarations.delete

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.{
  BaseElementDeclarableExtractors,
  ExtractorCommon
}
import org.mulesoft.als.common.SemanticNamedElement.ElementNameExtractor
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, FieldEntryImplicit}
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.lsp.edit.{ResourceOperation, TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common.{Location, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{BEGIN_DELETE_NODE_ACTION, END_DELETE_NODE_ACTION, MessageTypes}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.yaml.model.YPart

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteDeclaredNodeCodeAction(override val params: CodeActionRequestParams)
    extends CodeActionResponsePlugin
    with BaseElementDeclarableExtractors {

  override val isApplicable: Boolean =
    maybeTree.exists(t =>
      t.isDeclared &&
        t.fieldEntry.exists(_.isSemanticName)
    )

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] =
    Future {
      maybeTree
        .map(t => {
          val referencesEdits =
            nameLocation(t.obj).map(n => removeReferences(n, params.allRelationships)).getOrElse(Map.empty)
          val stringToEdits: Map[String, Seq[TextEdit]] = removeObj(t.obj)
            .map { ed =>
              referencesEdits.get(params.uri) match {
                case Some(seq) => referencesEdits.updated(params.uri, seq :+ ed)
                case _         => referencesEdits + (params.uri -> Seq(ed))
              }
            }
            .getOrElse(referencesEdits)
          AbstractWorkspaceEdit(editsToDocumentChanges(stringToEdits))
        })
        .map(we => DeleteDeclaredNodeCodeAction.baseCodeAction(we))
        .toSeq
    }

  // todo: change along with ALS-1257
  private def editsToDocumentChanges(
      changes: Map[String, Seq[TextEdit]]
  ): Seq[Either[TextDocumentEdit, ResourceOperation]] =
    changes.map { t =>
      Left(TextDocumentEdit(VersionedTextDocumentIdentifier(t._1, None), t._2))
    }.toSeq

  private def removeObj(obj: AmfObject) =
    ExtractorCommon
      .existAnyDeclaration(Seq(obj), yPartBranch, params.bu, params.definedBy)
      .map(toLspRange)
      .map(r => TextEdit(r.copy(start = r.start.copy(character = 0)), ""))
      .headOption

  private def nameLocation(obj: AmfObject): Option[Location] =
    obj
      .namedField()
      .flatMap(v =>
        v.annotations
          .ast()
          .orElse(v.value.annotations.ast())
          .map(p => p.yPartToLocation)
      )

  private def removeReferences(nameLocation: Location, r: Seq[RelationshipLink]): Map[String, Seq[TextEdit]] =
    r.filter(re => {
      re.targetEntry.yPartToLocation.uri == nameLocation.uri && re.targetEntry.range.contains(
        Position(nameLocation.range.start).toAmfPosition
      )
    }).map(rl =>
      rl.sourceEntry.location.sourceName -> TextEdit(
        toLspRange(PositionRange(rl.sourceEntry.range)),
        eolIfApplicable(rl.sourceEntry)
      )
    ).groupBy(_._1)
      .map(t => (t._1 -> t._2.map(_._2)))

  private def eolIfApplicable(sourceEntry: YPart): String = if (sourceEntry.range.columnTo == 0) "\n" else ""

  private def toLspRange(range: PositionRange) = LspRangeConverter.toLspRange(range)

  override protected def code(params: CodeActionRequestParams): String = "Delete declared node and references(cascade)"

  override protected def beginType(params: CodeActionRequestParams): MessageTypes = BEGIN_DELETE_NODE_ACTION

  override protected def endType(params: CodeActionRequestParams): MessageTypes = END_DELETE_NODE_ACTION

  override protected def msg(params: CodeActionRequestParams): String =
    s"Delete declared node: \n\t${params.uri}\t${params.range}"

  override protected def uri(params: CodeActionRequestParams): String = params.uri

  override protected val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    params.findDialectForSemantic

}

object DeleteDeclaredNodeCodeAction extends CodeActionFactory with DeleteDeclarationKind {
  override def apply(params: CodeActionRequestParams): CodeActionResponsePlugin =
    new DeleteDeclaredNodeCodeAction(params)
}
