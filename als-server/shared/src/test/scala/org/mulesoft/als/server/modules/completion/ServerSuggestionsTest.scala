package org.mulesoft.als.server.modules.completion

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.common.{MarkerFinderTest, MarkerInfo}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.completion.{CompletionItem, CompletionParams, CompletionRequestType}
import org.mulesoft.lsp.workspace.ExecuteCommandParams
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

  def runTest(path: String, expectedSuggestions: Set[String], dialectPath: Option[String] = None): Future[Assertion] =
    withServer[Assertion](buildServer(),
                          dialectPath.map(_ => initializeParamsCommandStyle).getOrElse(initializeParams)) { server =>
      val resolved = filePath(platform.encodeURI(path))
      for {
        content <- this.platform.fetchContent(resolved, AMFGraphConfiguration.predefined())
        suggestions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr, "*")

          dialectPath
            .map(p => filePath(platform.encodeURI(p)))
            .map { d =>
              commandRegisterDialect(d, server).flatMap(_ => getServerCompletions(resolved, server, markerInfo))
            }
            .getOrElse(getServerCompletions(resolved, server, markerInfo))
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

  def commandRegisterDialect(dialect: String, server: LanguageServer): Future[Unit] = {
    server.workspaceService
      .executeCommand(
        ExecuteCommandParams(Commands.DID_CHANGE_CONFIGURATION,
                             List(s"""{"mainUri": "", "dependencies": [{"file": "$dialect", "scope": "dialect"}]}""")))
      .flatMap(_ => Future.unit)
  }

  def getServerCompletions(filePath: String,
                           server: LanguageServer,
                           markerInfo: MarkerInfo): Future[Seq[CompletionItem]] = {

    openFile(server)(filePath, markerInfo.content)
      .flatMap { _ =>
        val completionHandler = server.resolveHandler(CompletionRequestType).value

        completionHandler(
          CompletionParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(markerInfo.position)))
          .flatMap(completions => {
            closeFile(server)(filePath)
              .map(_ => completions.left.value)
          })
      }

  }

}
