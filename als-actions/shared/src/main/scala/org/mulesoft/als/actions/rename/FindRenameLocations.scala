package org.mulesoft.als.actions.rename

import amf.core.model.document.BaseUnit
import org.mulesoft.amfintegration.relationships.AliasRelationships.FullLink
import org.mulesoft.amfintegration.relationships.{AliasInfo, RelationshipLink}
import org.mulesoft.als.actions.references.FindReferences
import org.mulesoft.als.common.YamlUtils
import org.mulesoft.als.common.cache.YPartBranchCached
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.{AmfAnnotationsImp, BaseUnitImp}
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.common.VersionedTextDocumentIdentifier
import org.yaml.model.{YMapEntry, YPart, YScalar}
import org.mulesoft.als.common.YamlWrapper._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FindRenameLocations {
  def changeDeclaredName(uri: String,
                         position: Position,
                         newName: String,
                         allAliases: Future[Seq[AliasInfo]],
                         references: Future[Seq[RelationshipLink]],
                         yPartBranchCached: YPartBranchCached,
                         unit: BaseUnit): Future[WorkspaceEdit] =
    FindReferences
      .getReferences(uri, position, allAliases, references, yPartBranchCached)
      .map { refs =>
        getOriginKey(unit, position)
          .fold(Seq[RenameLocation]())(refsToRenameLocation(newName, refs, _))
      }
      .map(_.groupBy(_.uri))
      .map { uriToLocation =>
        val stringToEdits = uriToLocation.mapValues(_.map(toTextEdit))
        WorkspaceEdit(
          stringToEdits,
          toTextDocumentEdit(stringToEdits).map(Left(_))
        )
      }

  private def refsToRenameLocation(newName: String, refs: Seq[FullLink], origKey: YScalar): Seq[RenameLocation] = {
    refs
      .map(t => RenameLocation(t._3, t._1.uri, PositionRange(t._1.range), newName, origKey.text)) :+
      RenameLocation(newName, origKey.location.sourceName, PositionRange(origKey.unmarkedRange()))
  }

  private def getOriginKey(unit: BaseUnit, position: Position): Option[YScalar] =
    unit.objWithAST
      .flatMap(_.annotations.ast())
      .map(YamlUtils.getNodeByPosition(_, position.toAmfPosition))
      .collect {
        case s: YScalar => s
      }

  private def toTextEdit(renameLocation: RenameLocation): TextEdit =
    TextEdit(LspRangeConverter.toLspRange(renameLocation.replaceRange), renameLocation.newName)

  private def toTextDocumentEdit(editsByUri: Map[String, Seq[TextEdit]]): Seq[TextDocumentEdit] =
    editsByUri.keys.map { uri =>
      TextDocumentEdit(VersionedTextDocumentIdentifier(uri, None), editsByUri(uri))
    }.toSeq
}

case class RenameLocation(newName: String, uri: String, replaceRange: PositionRange)

object RenameLocation {
  def apply(maybeYPart: Option[YPart],
            uri: String,
            replaceRange: PositionRange,
            newName: String,
            oldName: String): RenameLocation =
    maybeYPart match {
      case Some(yPart) =>
        val nodeContent = yPart.toString
        val i = nodeContent
          .lastIndexOf(oldName)
        val completeNewName: String =
          if (i == -1) newName
          else {
            val (pre, post) = nodeContent.splitAt(i)
            s"$pre$newName${post.substring(oldName.length)}"
          }
        RenameLocation(completeNewName, yPart.location.sourceName, PositionRange(yPart.range))
      case _ =>
        new RenameLocation(newName, uri, replaceRange)

    }
}
