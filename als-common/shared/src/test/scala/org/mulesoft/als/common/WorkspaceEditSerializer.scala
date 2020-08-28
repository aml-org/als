package org.mulesoft.als.common

import org.mulesoft.lsp.edit.{TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.common.Position
import org.yaml.model.YDocument
import org.yaml.model.YDocument.{EntryBuilder, PartBuilder}
import org.yaml.render.YamlRender

case class WorkspaceEditSerializer(edit: WorkspaceEdit) {

  def serialize(): String = toYaml(edit)

  private def toYaml(result: WorkspaceEdit): String = {
    val doc = YDocument.objFromBuilder(e => {
      e.entry("changes", p => {
        p.obj(eb => {
          result.changes.foreach(c => emitChangeForUri(c._1, c._2)(eb))
        })
      })

//      e.entry("documentChanges", p => {
//        // todo
//        if(result.documentChanges.nonEmpty) p + "nonEmptyDocumentChanges!!!"
//      })
    })
    YamlRender.render(doc)
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
    eb.entry("content", _.+=("+\n" + textEdit.newText))
  }

  private def emitPosition(position: Position)(eb: EntryBuilder): Unit = {
    eb.entry("line", position.line)
    eb.entry("column", position.character)
  }
}
