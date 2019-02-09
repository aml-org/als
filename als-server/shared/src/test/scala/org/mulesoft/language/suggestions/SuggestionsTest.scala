package org.mulesoft.language.suggestions

import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.language.common.dtoTypes.{OpenedDocument, Position}
import org.mulesoft.language.test.LanguageServerTest
import org.mulesoft.language.test.clientConnection.TestClientConnection
import org.scalatest.Assertion

import scala.concurrent.Future

abstract class SuggestionsTest extends LanguageServerTest {

  def runTest(path: String, expectedSuggestions: Set[String]): Future[Assertion] = {
    val resolved = filePath(path)
    init().flatMap { _ =>
      for {
        content <- this.platform.resolve(resolved)
        client <- getClient
        suggestions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo = this.findMarker(fileContentsStr)
          getClientSuggestions(resolved, client, markerInfo)
        }
      } yield {
        val resultSet = suggestions.map(_.text).toSet
        val diff1 = resultSet.diff(expectedSuggestions)
        val diff2 = expectedSuggestions.diff(resultSet)

        if (diff1.isEmpty && diff2.isEmpty) succeed
        else
          fail(s"Difference for $path: got [${resultSet.mkString(", ")}] while expecting [${expectedSuggestions.mkString(", ")}]")
      }
    }
  }

  def getClientSuggestions(filePath: String,
                           client: TestClientConnection,
                           markerInfo: MarkerInfo): Future[Seq[Suggestion]] = {
    client.documentOpened(OpenedDocument(filePath, 0, markerInfo.rawContent))
    client
      .getSuggestions(filePath, markerInfo.position)
      .map(suggestions => {
        client.documentClosed(filePath)
        suggestions
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
