package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.core.model.domain.AmfObject
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionRequestParams, CodeActionResponsePlugin}
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.ExtractSameFileDeclaration
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit}
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, DialectImplicits}
import org.mulesoft.lsp.feature.common.{Position, VersionedTextDocumentIdentifier}
import org.yaml.model.{YMapEntry, YPart}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ConverterExtractor[Original <: AmfObject, Result <: AmfObject]
    extends CodeActionResponsePlugin
    with ExtractSameFileDeclaration {

  protected val kindTitle: CodeActionKindTitle

  override protected lazy val amfObject: Option[AmfObject] = maybeTree.flatMap(o => Some(o.obj))

  def original: Option[Original]

  lazy val declarationResult: Option[Result] = original.map(transform)

  override protected lazy val entryAst: Option[YPart] =
    amfObject.flatMap(_.annotations.ast())

  def transform(original: Original): Result

  override protected lazy val declaredElementTextEdit: Option[TextEdit] =
    renderDeclaredEntry(declarationResult, newName)
      .map(de => TextEdit(rangeFromEntryBottom(de._2), s"\n${de._1}\n"))

  def modifyEntry(original: Original): String

  def targetTextEdit(opStr: Option[String]): Option[TextEdit] = {
    opStr.flatMap(str => entryRange.map(r => r.copy(Position(r.start.line, 0))).map(TextEdit(_, str)))
  }
  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] = {
    Future {
      targetTextEdit(original.map(modifyEntry))
        .flatMap(
          editOriginalEntry =>
            declaredElementTextEdit
              .map(Seq(editOriginalEntry, _))
              .map(edits => {
                kindTitle.baseCodeAction(AbstractWorkspaceEdit(
                  Seq(Left(TextDocumentEdit(VersionedTextDocumentIdentifier(params.uri, None), edits)))))
              }))
        .toSeq
    }
  }

}
