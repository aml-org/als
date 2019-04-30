package org.mulesoft.als.server.modules.completion

import common.dtoTypes.Position
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.common.LspConverter.toLspPosition
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.als.server.platform.ServerPlatform
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.completion.{CompletionItem, CompletionParams, CompletionRequestType}
import org.mulesoft.lsp.server.LanguageServer
import org.scalatest.{Assertion, EitherValues}

import scala.concurrent.Future

abstract class ServerSuggestionsTest extends LanguageServerBaseTest with EitherValues {

  override def addModules(documentManager: TextDocumentManager,
                          serverPlatform: ServerPlatform,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val astManager = new AstManager(documentManager, serverPlatform, logger)
    val hlAstManager = new HlAstManager(documentManager, astManager, serverPlatform, logger)
    val completionManager = new SuggestionsManager(documentManager, hlAstManager, serverPlatform, logger)

    builder
      .addInitializable(astManager)
      .addInitializable(hlAstManager)
      .addRequestModule(completionManager)
  }

  def runTest(path: String, expectedSuggestions: Set[String]): Future[Assertion] = withServer[Assertion]{ server =>
    val resolved = filePath(path)
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
        val diff1     = resultSet.diff(expectedSuggestions)
        val diff2     = expectedSuggestions.diff(resultSet)

        if (diff1.isEmpty && diff2.isEmpty) succeed
        else
          fail(
            s"Difference for $path: got [${resultSet.mkString(", ")}] while expecting [${expectedSuggestions.mkString(", ")}]")
      }
  }

  def getServerCompletions(filePath: String,
                           server: LanguageServer,
                           markerInfo: MarkerInfo): Future[Seq[CompletionItem]] = {

    openFile(server)(filePath, markerInfo.rawContent)

    val completionHandler = server.resolveHandler(CompletionRequestType).value

    completionHandler(CompletionParams(
      TextDocumentIdentifier(filePath),
      toLspPosition(markerInfo.position)))
        .map(completions => {
          closeFile(server)(filePath)

          completions.left.value
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
