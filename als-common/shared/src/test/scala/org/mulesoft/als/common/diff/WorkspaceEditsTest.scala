package org.mulesoft.als.common.diff

import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.scalatest.Assertion
import org.scalatest.Matchers.{fail, succeed}
import scala.concurrent.{ExecutionContext, Future}

trait WorkspaceEditsTest extends FileAssertionTest {
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  def assertWorkspaceEdits(workspaceEdit: WorkspaceEdit,
                           goldenPath: String,
                           content: Option[String]): Future[Assertion] = {

    val newText = applyEdits(workspaceEdit, content)
    for {
      tmp <- writeTemporaryFile(goldenPath)(newText)
      r   <- assertDifferences(tmp, goldenPath)
    } yield r
  }

  def applyEdits(workspaceEdit: WorkspaceEdit, content: Option[String], uri: Option[String] = None): String = {
    val edits = workspaceEdit.changes
      .getOrElse(Nil)
      .flatMap {
        case (u, textEdits) if uri.forall(_ == u) => textEdits
        case _                                    => Nil
      }
      .toList

    var newText = content.get
    val sortedEdits = edits
      .sortWith((a, b) => LspRangeConverter.toPosition(a.range.start) > LspRangeConverter.toPosition(b.range.start))
    sortedEdits
      .foreach(
        edit =>
          newText = newText.substring(0, LspRangeConverter.toPosition(edit.range.start).offset(newText)) +
            edit.newText +
            newText.substring(LspRangeConverter.toPosition(edit.range.end).offset(newText)))
    newText
  }

  def applyEdits(workspaceEdit: WorkspaceEdit, ws: Map[String, String]): Map[String, String] = {
    var result: Map[String, String] = Map()
    ws.foreach { a =>
      val edit = applyEdits(workspaceEdit, Some(a._2), Some(a._1))
      result = result + (a._1 -> edit)
    }
    result
  }
}
