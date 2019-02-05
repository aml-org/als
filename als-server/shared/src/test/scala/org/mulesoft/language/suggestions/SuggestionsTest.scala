package org.mulesoft.language.suggestions

import org.mulesoft.als.suggestions.interfaces.ISuggestion
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.language.common.dtoTypes.IOpenedDocument
import org.mulesoft.language.test.LanguageServerTest
import org.mulesoft.language.test.clientConnection.TestClientConnetcion
import org.scalatest.Assertion

import scala.concurrent.Future

abstract class SuggestionsTest extends LanguageServerTest {

  def runTest(path: String, expectedSuggestions: Set[String]): Future[Assertion] = {
    val resolved = filePath(path)
    init().flatMap { _ =>
      for {
        content <- this.platform.resolve(resolved)
        client  <- getClient
        suggestions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr)
          getClientSuggestions(resolved, client, markerInfo)
        }
      } yield {
        val resultSet = suggestions.map(_.text).toSet
        val diff1     = resultSet.diff(expectedSuggestions)
        val diff2     = expectedSuggestions.diff(resultSet)

        if (diff1.isEmpty && diff2.isEmpty) succeed
        else
          fail(
            s"Difference for $path: got [${resultSet.mkString(", ")}] while expecting [${expectedSuggestions.mkString(", ")}]")
      }
    }
  }

  def getClientSuggestions(filePath: String,
                           client: TestClientConnetcion,
                           markerInfo: MarkerInfo): Future[Seq[ISuggestion]] = {
    client.documentOpened(IOpenedDocument(filePath, 0, markerInfo.rawContent))
    client
      .getSuggestions(filePath, markerInfo.position)
      .map(suggestions => {
        client.documentClosed(filePath)
        suggestions
      })
  }

  def findMarker(str: String, label: String = "*", cut: Boolean = true): MarkerInfo = {

    val position = str.indexOf(label)

    if (position < 0) {
      new MarkerInfo(str, str.length, str)
    } else {
      val rawContent = str.substring(0, position) + str.substring(position + 1)
      val preparedContent =
        org.mulesoft.als.suggestions.Core.prepareText(rawContent, position, YAML)
      new MarkerInfo(preparedContent, position, rawContent)
    }

  }
}

class MarkerInfo(val content: String, val position: Int, val rawContent: String) {}
