package org.mulesoft.als.common

import org.mulesoft.lsp.edit.{
  CreateFile,
  DeleteFile,
  RenameFile,
  ResourceOperation,
  TextDocumentEdit,
  TextEdit,
  WorkspaceEdit
}
import org.mulesoft.lsp.feature.common.Position
import org.yaml.model.YDocument
import org.yaml.model.YDocument.{EntryBuilder, PartBuilder}
import org.yaml.render.YamlRender

case class WorkspaceEditSerializer(edit: WorkspaceEdit) {

  def serialize(): String = toYaml

  def entryChanges(e: EntryBuilder): Unit = {
    edit.changes.foreach(changes =>
      e.entry(
        "changes",
        p => {
          p.obj(eb => {
            changes.foreach(c => emitChangeForUri(c._1, c._2)(eb))
          })
        }
      )
    )

    edit.documentChanges.foreach(documentChanges =>
      e.entry(
        "documentChanges",
        p => {
          p.obj(eb => {
            documentChanges.foreach {
              case Left(value)  => emitChangeForTextDocumentEdit(value)(eb)
              case Right(value) => emitChangeForResource(value)(eb)
            }
          })
        }
      )
    )
  }

  private def toYaml: String = {
    val doc = YDocument.objFromBuilder(e => {
      entryChanges(e)
    })
    YamlRender.render(doc)
  }

  private def emitChangeForTextDocumentEdit(edit: TextDocumentEdit)(eb: EntryBuilder): Unit = {
    eb.entry(edit.textDocument.uri, pb => emitTextEdits(edit.edits)(pb))
  }

  private def emitChangeForResource(operation: ResourceOperation)(eb: EntryBuilder): Unit = {
    operation match {
      case CreateFile(uri, options)            => eb.entry(uri, "create")
      case RenameFile(oldUri, newUri, options) => eb.entry(oldUri, newUri)
      case DeleteFile(uri, options)            => eb.entry(uri, "delete")
    }
  }

  private def emitChangeForUri(uri: String, textEdits: Seq[TextEdit])(eb: EntryBuilder): Unit = {
    eb.entry(uri, pb => emitTextEdits(textEdits)(pb))
  }

  private def emitTextEdits(textEdits: Seq[TextEdit])(pb: PartBuilder): Unit = {
    pb.list(p => {
      textEdits.foreach(te => p.obj(emitTextEdit(te)))
    })
  }

  private def emitTextEdit(textEdit: TextEdit)(eb: EntryBuilder): Unit = {
    eb.entry("from", _.obj(emitPosition(textEdit.range.start)))
    eb.entry("to", _.obj(emitPosition(textEdit.range.end)))
    eb.entry("content", _.+=(addEolIfNecessary(textEdit.newText)))
  }

  /** This method was implemented to avoid the wrongful modification of golden files on OAS3 when content starts with
    * '\n' (this happens on Delete Declared Node Code Action is excecuted)
    *
    * @param content
    *   TextEdit content
    * @return
    *   fixed content on '\n' cases
    */
  private def addEolIfNecessary(content: String) =
    if (content == "\n") s"+$content" else s"+\n$content"

  private def emitPosition(position: Position)(eb: EntryBuilder): Unit = {
    eb.entry("line", position.line)
    eb.entry("column", position.character)
  }
}
