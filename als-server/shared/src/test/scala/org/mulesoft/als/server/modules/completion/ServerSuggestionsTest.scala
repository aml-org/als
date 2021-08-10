package org.mulesoft.als.server.modules.completion

import org.mulesoft.als.common.{MarkerFinderTest, MarkerInfo}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.completion.{CompletionItem, CompletionParams, CompletionRequestType}
import org.scalatest.{Assertion, EitherValues}

import scala.concurrent.Future

abstract class ServerSuggestionsTest extends LanguageServerBaseTest with EitherValues with MarkerFinderTest {

  def buildServer(): LanguageServer = {

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.completionManager)
      .build()
  }

  def runTest(path: String, expectedSuggestions: Set[String]): Future[Assertion] =
    withServer[Assertion](buildServer()) { server =>
      val resolved = filePath(platform.encodeURI(path))
      for {
        content <- this.platform.resolve(resolved)
        suggestions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr, "*")

          getServerCompletions(resolved, server, markerInfo)
        }
      } yield {
        val resultSet = suggestions
          .map(item => item.textEdit.map(_.left.get.newText).orElse(item.insertText).value)
          .toSet
        val diff1 = resultSet.diff(expectedSuggestions)
        val diff2 = expectedSuggestions.diff(resultSet)

        if (diff1.isEmpty && diff2.isEmpty) succeed
        else
          fail(
            s"Difference for $path: got [${resultSet.mkString(", ")}] while expecting [${expectedSuggestions.mkString(", ")}]")
      }
    }

  def getServerCompletions(filePath: String,
                           server: LanguageServer,
                           markerInfo: MarkerInfo): Future[Seq[CompletionItem]] = {

    openFile(server)(filePath, markerInfo.content)

    val completionHandler = server.resolveHandler(CompletionRequestType).value

    completionHandler(
      CompletionParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(markerInfo.position)))
      .map(completions => {
        closeFile(server)(filePath)

        completions.left.value
      })
  }

}
