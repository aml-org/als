package org.mulesoft.als.actions.rangeFormatting

import amf.core.parser.Range
import org.mulesoft.als.actions.formatting.RangeFormatting
import org.mulesoft.als.common.diff.WorkspaceEditsTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{ByDirectoryTest, MarkerFinderTest, NodeBranchBuilder, YamlWrapper}
import org.mulesoft.common.io.{Fs, SyncFile}
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.{TextEdit, WorkspaceEdit}
import org.yaml.model.{YDocument, YPart}

import scala.concurrent.ExecutionContext

trait RangeFormattingTest extends ByDirectoryTest with MarkerFinderTest with WorkspaceEditsTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def basePath: String

  def dir: SyncFile = Fs.syncFile(basePath)

  def parse(content: String): YDocument

  def isJson: Boolean

  def filePath(path: String): String = {
    s"file://$basePath/$path"
      .replace('\\', '/')
      .replace("null/", "")
  }

  override def testFile(rawContent: String, file: SyncFile, parent: String): Unit = {
    s"Range formatting on ${file.name} at dir $parent${dir.name}" in {
      val expectedUri = file.parent + "/expected/" + file.name
      val markers     = findMarkers(rawContent)
      val content     = markers.headOption.map(_.content).getOrElse(rawContent)

      val (ypart, indentation): (YPart, Int) = markers.headOption
        .map(start => {
          assert(markers.length == 2)
          val ast = parse(start.content)
          val end = markers.tail.head
          val part =
            NodeBranchBuilder.getAstForRange(ast, start.position.toAmfPosition, end.position.toAmfPosition, isJson)
          val indentation = YamlWrapper.getIndentation(content, Position(Range(part.range).start))

          (part, indentation)
        })
        .getOrElse((parse(content), 0))

      val formattingOption     = FormattingOptions(2, insertSpaces = true)
      val edits: Seq[TextEdit] = RangeFormatting(ypart, formattingOption, indentation, isJson).format()

      writeTemporaryFile(expectedUri)(applyEdits(WorkspaceEdit(Some(Map(file.name -> edits)), None), Option(content)))
        .flatMap(tmp => assertDifferences(tmp, expectedUri))
    }
  }
}
