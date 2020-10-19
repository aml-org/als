package org.mulesoft.als.common.diff

import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.scalatest.Assertion
import org.scalatest.Matchers.{fail, succeed}

trait WorkspaceEditsTest {

  def assertWorkspaceEdits(workspaceEdit: WorkspaceEdit,
                           golden: Option[String],
                           content: Option[String],
                           path: String): Assertion = {
    val edits = workspaceEdit.changes.getOrElse(Nil).flatMap { case (_, textEdits) => textEdits }.toList

    var newText = content.get
    val sortedEdits = edits
      .sortWith((a, b) => LspRangeConverter.toPosition(a.range.start) > LspRangeConverter.toPosition(b.range.start))
    sortedEdits
      .foreach(
        edit =>
          newText = newText.substring(0, LspRangeConverter.toPosition(edit.range.start).offset(newText)) +
            edit.newText +
            newText.substring(LspRangeConverter.toPosition(edit.range.end).offset(newText)))
    val diffs = Diff.ignoreAllSpace.diff(golden.get, newText.trim)
    if (diffs.isEmpty) succeed
    else {
      println(diffs.mkString)
      fail(s"Difference for $path: got [${newText.trim}] while expecting [${golden.get}]")
    }
  }
}
