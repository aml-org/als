package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionRequestParams, CodeActionResponsePlugin}
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.ExtractSameFileDeclaration
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common.VersionedTextDocumentIdentifier
import org.yaml.model.{YMapEntry, YNode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ConverterExtractor[Original <: AmfObject, Result <: AmfObject]
    extends CodeActionResponsePlugin
    with ExtractSameFileDeclaration
    with DeclarationWrapper {

  protected val kindTitle: CodeActionKindTitle

  override protected lazy val amfObject: Option[AmfObject] = maybeTree.flatMap(o => Some(o.obj))

  def original: Option[Original]

  lazy val declarationResult: Option[Result] = original.map(transform)

  def transform(original: Original): Result

  override protected lazy val declaredElementTextEdit: Option[TextEdit] =
    wrapDeclaration(
      declarationResult,
      newName,
      params.bu,
      params.uri,
      params.documentDefinition,
      params.configuration,
      jsonOptions,
      yamlOptions,
      params.alsConfigurationState
    ).map(de =>
      TextEdit(rangeFromEntryBottom(de._2).getOrElse(LspRangeConverter.toLspRange(afterInfoRange)), s"\n${de._1}\n")
    )

  def modifyEntry(original: Original): String

  def targetTextEdit(opStr: Option[String]): Option[TextEdit] =
    opStr.flatMap(str => entryRange.map(TextEdit(_, str)))

  protected def renderNode(node: YNode, maybeParent: Option[YMapEntry]): String =
    ExtractorCommon.renderNode(node, maybeParent, params.bu, params.configuration, jsonOptions, yamlOptions)._1

  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] = {
    Future {
      targetTextEdit(original.map(modifyEntry))
        .flatMap(editOriginalEntry =>
          declaredElementTextEdit
            .map(Seq(editOriginalEntry, _))
            .map(edits => {
              kindTitle.baseCodeAction(
                AbstractWorkspaceEdit(
                  Seq(Left(TextDocumentEdit(VersionedTextDocumentIdentifier(params.uri, None), edits)))
                )
              )
            })
        )
        .toSeq
    }
  }

}
