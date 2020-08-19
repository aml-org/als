package org.mulesoft.als.actions.codeactions.plugins.declarations

import org.mulesoft.als.actions.codeactions.plugins.base.{
  CodeActionFactory,
  CodeActionRequestParams,
  CodeActionResponsePlugin
}
import org.mulesoft.als.actions.codeactions.plugins.declarations.ExtractElementCodeAction.baseCodeAction
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.codeactions.CodeActionKind.CodeActionKind
import org.mulesoft.lsp.feature.codeactions.{CodeAction, CodeActionKind}
import org.mulesoft.lsp.feature.common
import org.mulesoft.lsp.feature.common.{Position, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_EXTRACT_ELEMENT_ACTION,
  END_EXTRACT_ELEMENT_ACTION,
  MessageTypes
}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.yaml.model.{YMapEntry, YNonContent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ExtractElementCodeAction(params: CodeActionRequestParams, override val kind: CodeActionKind)
    extends CodeActionResponsePlugin
    with BaseDeclarableExtractors {

  override protected def telemetry: TelemetryProvider = params.telemetryProvider

  private lazy val declaredElementTextEdit: Option[TextEdit] =
    declaredEntry
      .map(de => TextEdit(rangeFromEntryBottom(de._2), s"\n${de._1}\n"))

  protected def rangeFromEntryBottom(maybeEntry: Option[YMapEntry]): common.Range =
    maybeEntry.flatMap(_.children.filterNot(_.isInstanceOf[YNonContent]).lastOption) match {
      case Some(e) =>
        val pos = PositionRange(e.range).`end`
        LspRangeConverter.toLspRange(PositionRange(pos, pos))
      case None => common.Range(Position(1, 0), Position(1, 0))
    }

  override protected def task(params: CodeActionRequestParams): Future[Seq[CodeAction]] = Future {
    linkEntry
      .flatMap(e => declaredElementTextEdit.map(Seq(e, _)))
      .map(edits => {
        baseCodeAction(
          WorkspaceEdit(Map(params.uri -> edits),
                        Seq(Left(TextDocumentEdit(VersionedTextDocumentIdentifier(params.uri, None), edits)))))
      })
      .toSeq
  }

  override protected def code(params: CodeActionRequestParams): String =
    "extract declared element code action"

  override protected def beginType(params: CodeActionRequestParams): MessageTypes =
    BEGIN_EXTRACT_ELEMENT_ACTION

  override protected def endType(params: CodeActionRequestParams): MessageTypes =
    END_EXTRACT_ELEMENT_ACTION

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
