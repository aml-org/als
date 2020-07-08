package org.mulesoft.als.actions.rename

import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.actions.references.FindReferences
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.edit.{TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.common.{Range, VersionedTextDocumentIdentifier}
import org.yaml.model.{YMapEntry, YNode, YPart, YScalar}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FindRenameLocations {

  def canChangeDeclaredName(uri: String,
                            position: Position,
                            references: Future[Seq[RelationshipLink]]): Future[Option[Range]] =
    FindReferences
      .getReferences(uri, position, references)
      .map { refs =>
        getOriginKey(refs)
          .map(_.range)
          .map(PositionRange(_))
          .map(LspRangeConverter.toLspRange)
      }

  def changeDeclaredName(uri: String,
                         position: Position,
                         newName: String,
                         references: Future[Seq[RelationshipLink]]): Future[WorkspaceEdit] =
    FindReferences
      .getReferences(uri, position, references)
      .map { refs =>
        getOriginKey(refs)
          .map { origKey =>
            refs
              .map(_.sourceEntry)
              .map(value)
              .map(RenameLocation(_, newName, origKey.text)) :+
              RenameLocation(origKey, newName, origKey.text)
          }
          .getOrElse(Seq.empty)
      }
      .map(_.groupBy(_.uri))
      .map { uriToLocation =>
        val stringToEdits = uriToLocation.mapValues(_.map(toTextEdit))
        WorkspaceEdit(
          stringToEdits,
          toTextDocumentEdit(stringToEdits).map(Left(_))
        )
      }

  private def getOriginKey(refs: Seq[RelationshipLink]): Option[YScalar] =
    getScalarFromName(refs) orElse getScalarFromEntry(refs)

  private def getScalarFromEntry(refs: Seq[RelationshipLink]) =
    refs
      .map(_.targetEntry)
      .collectFirst {
        case e: YMapEntry => e.key
      }
      .flatMap(_.asScalar)

  private def getScalarFromName(refs: Seq[RelationshipLink]) =
    refs.flatMap(_.nameRange).headOption.flatMap {
      case n: YNode => n.asScalar
      case _        => None
    }

  private def toTextEdit(renameLocation: RenameLocation): TextEdit =
    TextEdit(LspRangeConverter.toLspRange(renameLocation.replaceRange), renameLocation.newName)

  private def toTextDocumentEdit(editsByUri: Map[String, Seq[TextEdit]]): Seq[TextDocumentEdit] =
    editsByUri.keys.map { uri =>
      TextDocumentEdit(VersionedTextDocumentIdentifier(uri, None), editsByUri(uri))
    }.toSeq

  private def value(yPart: YPart): YPart =
    yPart match {
      case yMapEntry: YMapEntry => yMapEntry.value
      case _                    => yPart
    }
}

case class RenameLocation(newName: String, uri: String, replaceRange: PositionRange)

object RenameLocation {
  def apply(yPart: YPart, newName: String, oldName: String): RenameLocation = {
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
  }

}
