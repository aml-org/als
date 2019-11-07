package org.mulesoft.als.server.modules.completion

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.workspace.WorkspaceRootHandler
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.ContentPatcher
import org.mulesoft.als.suggestions.patcher.PatchedContent
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.completion.{CompletionItem, CompletionParams, CompletionRequestType}
import org.mulesoft.lsp.server.LanguageServer
import org.scalatest.{Assertion, EitherValues}

import scala.concurrent.Future

abstract class ServerSuggestionsTest extends LanguageServerBaseTest with EitherValues {

  override def buildServer(): LanguageServer = {

    val factory = ManagersFactory(MockDiagnosticClientNotifier, new WorkspaceRootHandler(platform), platform, logger)
    new LanguageServerBuilder(factory.documentManager, platform)
      .addInitializable(factory.astManager)
      .addRequestModule(factory.completionManager)
      .build()
  }

  def runTest(path: String, expectedSuggestions: Set[String]): Future[Assertion] = withServer[Assertion] { server =>
    val resolved = filePath(platform.encodeURI(path))
    for {
      content <- this.platform.resolve(resolved)
      suggestions <- {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)

        getServerCompletions(resolved, server, markerInfo)
      }
    } yield {
      val resultSet = suggestions
        .map(item => item.textEdit.map(_.newText).orElse(item.insertText).value)
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

    openFile(server)(filePath, markerInfo.patchedContent.original)

    val completionHandler = server.resolveHandler(CompletionRequestType).value

    completionHandler(
      CompletionParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(markerInfo.position)))
      .map(completions => {
        closeFile(server)(filePath)

        completions.left.value
      })
  }

  def findMarker(str: String, label: String = "*", cut: Boolean = true): MarkerInfo = {
    val offset = str.indexOf(label)

    if (offset < 0)
      new MarkerInfo(PatchedContent(str, str, Nil), Position(str.length, str))
    else {
      val rawContent      = str.substring(0, offset) + str.substring(offset + 1)
      val preparedContent = ContentPatcher(rawContent, offset, YAML).prepareContent()
      new MarkerInfo(preparedContent, Position(offset, str))
    }
  }
}

class MarkerInfo(val patchedContent: PatchedContent, val position: Position) {}
