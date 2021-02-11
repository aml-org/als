package org.mulesoft.als.actions.renamefile

import amf.core.remote.Platform
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.als.common.edits.AbstractWorkspaceEdit
import org.mulesoft.lsp.edit._
import org.mulesoft.lsp.feature.common.{
  TextDocumentIdentifier,
  VersionedTextDocumentIdentifier,
  Position => LspPosition,
  Range => LspRange
}
import org.mulesoft.lsp.feature.link.DocumentLink

object RenameFileAction {

  def renameFileEdits(oldDocument: TextDocumentIdentifier,
                      newDocument: TextDocumentIdentifier,
                      links: Map[String, Seq[DocumentLink]],
                      platform: Platform): AbstractWorkspaceEdit = {

    val oldFileName = oldDocument.uri.toAmfDecodedUri(platform).split("/").last
    val newFileName = newDocument.uri.toAmfDecodedUri(platform).split("/").last
    val documentChanges: Seq[Either[TextDocumentEdit, ResourceOperation]] =
      Seq(Right(RenameFile(oldDocument.uri, newDocument.uri, None))) ++
        links
          .map {
            case (file, links) =>
              getTextDocumentsEdits(oldDocument.uri, file, oldFileName, newFileName, links)
          }
          .filterNot(_.edits.isEmpty)
          .map(Left(_))

    AbstractWorkspaceEdit(documentChanges)
  }

  private def getTextDocumentsEdits(renamedUri: String,
                                    file: String,
                                    oldFileName: String,
                                    newFileName: String,
                                    links: Seq[DocumentLink]): TextDocumentEdit =
    TextDocumentEdit(
      VersionedTextDocumentIdentifier(file, None),
      links
        .filter(_.target == renamedUri)
        .map(link => {
          val replaceRange: LspRange = LspRange(
            LspPosition(link.range.end.line, link.range.end.character - oldFileName.length),
            link.range.end
          )
          TextEdit(replaceRange, newFileName)
        })
    )
}
