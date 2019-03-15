package org.mulesoft.language.rename

import common.dtoTypes.{DescendingPositionOrdering, Position}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.language.common.dtoTypes.{OpenedDocument, TextEdit}
import org.mulesoft.language.test.LanguageServerTest
import org.scalatest.Assertion

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

abstract class RenameTest extends LanguageServerTest {

  def runTest(path: String, newName: String): Future[Assertion] = {
    init().flatMap(_ => {
      val resultPath = path.replace(".", "-renamed.")
      val resolved = filePath(path)
      val resolvedResultPath = filePath(resultPath)
      var renamedContent: Option[String] = None
      var content: Option[String] = None
      Future
        .sequence(List(this.platform.resolve(resolved), this.platform.resolve(resolvedResultPath)))
        .flatMap(contents => {

          val fileContentsStr = contents.head.stream.toString
          val renamedFileContentsStr = contents.last.stream.toString
          renamedContent = Option(renamedFileContentsStr.trim)
          val markerInfo = this.findMarker(fileContentsStr)
          content = Option(markerInfo.rawContent)
          val position = markerInfo.position
          getClient.flatMap(client => {
            val filePath = s"file:///$path"
            client.documentOpened(OpenedDocument(filePath, 0, markerInfo.rawContent))
            client.rename(filePath, position, newName).map(changedDocs => {
              client.documentClosed(filePath)
              changedDocs
            })
          })
        }).map(changedDocs => {
        var edits: ListBuffer[TextEdit] = ListBuffer()
        changedDocs.foreach(x => x.textEdits.map(edits ++= _))
        var newText = content.get
        edits.sortBy(_.range.start)(DescendingPositionOrdering)
          .foreach(te =>
            newText = newText.substring(0, te.range.start.offset(newText)) + te.text + newText.substring(te.range.end.offset(newText))
          )
        val result = renamedContent.contains(newText.trim)

        if (result) succeed
        else fail(s"Difference for $path: got [$newText] while expecting [${renamedContent.get}]")
      })
    })
  }

  def findMarker(str: String, label: String = "*", cut: Boolean = true): MarkerInfo = {

    val offset = str.indexOf(label)

    if (offset < 0) {
      new MarkerInfo(str, Position(str.length, str), str)
    } else {
      val rawContent = str.substring(0, offset) + str.substring(offset + 1)
      val preparedContent =
        org.mulesoft.als.suggestions.Core.prepareText(rawContent, offset, YAML)
      new MarkerInfo(preparedContent, Position(offset, str), rawContent)
    }

  }
}

class MarkerInfo(val content: String, val position: Position, val rawContent: String) {}
