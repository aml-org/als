package org.mulesoft.als.actions.codeactions.plugins.declarations.samefile

import amf.core.client.scala.model.domain.{AmfObject, DomainElement, Linkable}
import amf.core.internal.annotations.DeclaredElement
import amf.shapes.internal.annotations.ForceEntry
import org.mulesoft.als.actions.codeactions.plugins.CodeActionKindTitle
import org.mulesoft.als.actions.codeactions.plugins.base.{CodeActionRequestParams, CodeActionResponsePlugin}
import org.mulesoft.als.actions.codeactions.plugins.conversions.ShapeExtractor
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.als.common.edits.codeaction.AbstractCodeAction
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit}
import org.mulesoft.lsp.feature.common.{Position, Range, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.{
  BEGIN_EXTRACT_ELEMENT_ACTION,
  END_EXTRACT_ELEMENT_ACTION,
  MessageTypes
}
import org.yaml.model.{YMap, YMapEntry, YNode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ExtractSameFileDeclaration extends CodeActionResponsePlugin with ShapeExtractor {
  protected val kindTitle: CodeActionKindTitle

  protected def rangeFromEntryBottom(maybeEntry: Option[YMapEntry]): Option[Range] =
    maybeEntry
      .map(_.value.value)
      .collect { case m: YMap => m }
      .flatMap(_.entries.lastOption)
      .map { e =>
        val pos = PositionRange(e.range).`end`
        LspRangeConverter.toLspRange(PositionRange(pos, pos))
      }

  protected def appliesToDocument(): Boolean =
    !params.bu.isFragment || params.bu
      .documentMapping(params.definedBy)
      .exists(_.declaredNodes().exists(dn => amfObject.exists(_.id == dn.mappedNode().value())))

  protected lazy val declaredElementTextEdit: Option[TextEdit] =
    renderDeclaredEntry(amfObject, newName)
      .map(de =>
        TextEdit(rangeFromEntryBottom(de._2).getOrElse(LspRangeConverter.toLspRange(afterInfoRange)), s"\n${de._1}\n")
      )

  protected def renderDeclaredEntry(amfObject: Option[AmfObject], name: String): Option[(String, Option[YMapEntry])] =
    ExtractorCommon
      .declaredEntry(
        amfObject,
        params.definedBy,
        params.bu,
        params.uri,
        name,
        params.configuration,
        jsonOptions,
        yamlOptions,
        params.alsConfigurationState
      )

  protected lazy val homogeneousVendor: Boolean =
    maybeTree
      .flatMap(_.objSpec(params.alsConfigurationState.findSemanticByName))
      .forall(params.bu.sourceSpec.contains)

  override protected def task(params: CodeActionRequestParams): Future[Seq[AbstractCodeAction]] =
    linkEntry.map {
      _.flatMap(e => declaredElementTextEdit.map(Seq(e, _)))
        .map(edits => {
          kindTitle.baseCodeAction(
            AbstractWorkspaceEdit(Seq(Left(TextDocumentEdit(VersionedTextDocumentIdentifier(params.uri, None), edits))))
          )
        })
        .toSeq
    }

  override protected lazy val renderLink: Future[Option[YNode]] = Future {
    amfObject
      .collect { case l: Linkable =>
        if (!l.annotations.isDeclared)
          l.annotations += DeclaredElement()
        val linkDe: DomainElement = l.link(newName)
        linkDe.annotations += ForceEntry() // raml explicit types
        params.alsConfigurationState.configForDialect(params.definedBy).emit(linkDe)
      }
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
