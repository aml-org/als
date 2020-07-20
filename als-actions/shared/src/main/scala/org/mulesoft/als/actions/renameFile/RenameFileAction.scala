package org.mulesoft.als.actions.renameFile

import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.actions.fileusage.FindFileUsages
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.edit.{RenameFile, ResourceOperation, TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.common.{Location, TextDocumentIdentifier, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RenameFileAction {

  def renameFileEdits(oldDocument: TextDocumentIdentifier,
                      newDocument: TextDocumentIdentifier,
                      links: Map[String, Seq[DocumentLink]],
                      uriToNewFile: Option[String]): WorkspaceEdit = {

    val oldFileName = oldDocument.uri.split("/").last
    val newFileName = newDocument.uri.split("/").last
    val documentChanges: Seq[Either[TextDocumentEdit, ResourceOperation]] =
      Seq(Right(RenameFile(oldDocument.uri, newDocument.uri, None))) ++
        links
          .map {
            case (file, links) =>
              getTextDocumentsEdits(file, oldFileName, uriToNewFile.getOrElse(newFileName), links)
          }
          .filterNot(_.edits.isEmpty)
          .map(Left(_))

    WorkspaceEdit(Map(), documentChanges)
  }

  private def getTextDocumentsEdits(file: String,
                                    oldFileName: String,
                                    newFileName: String,
                                    links: Seq[DocumentLink]): TextDocumentEdit = {

    TextDocumentEdit(
      VersionedTextDocumentIdentifier(file, None),
      Seq(
        links
          .filter(_.target.endsWith(oldFileName))
          .map(link => {
            TextEdit(LspRangeConverter.toLspRange(PositionRange(link.range)), newFileName)
          })).flatten
    )
  }
}
