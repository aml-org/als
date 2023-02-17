package org.mulesoft.als.server.modules.rename

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.common.MarkerFinderTest
import org.mulesoft.als.common.diff.WorkspaceEditsTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.rename.{PrepareRenameParams, PrepareRenameRequestType, RenameParams, RenameRequestType}
import org.scalatest.compatible.Assertion

import scala.concurrent.{ExecutionContext, Future}

abstract class ServerRenameTest extends LanguageServerBaseTest with WorkspaceEditsTest with MarkerFinderTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
      .addInitializable(factory.documentManager)
      .addRequestModule(factory.renameManager)
      .build()
  }

  def runTest(path: String, newName: String): Future[Assertion] = withServer[Assertion](buildServer()) { server =>
    val resultPath              = path.replace(".", "-renamed.")
    val original                = filePath(path)
    val goldenPath              = filePath(resultPath)
    var content: Option[String] = None

    platform
      .fetchContent(original, AMFGraphConfiguration.predefined())
      .flatMap(contents => {

        val fileContentsStr = contents.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr, "*")
        content = Option(markerInfo.content)
        val position = markerInfo.position

        val filePath = s"file:///$path"
        openFile(server)(filePath, markerInfo.content)
          .flatMap(_ =>
            prepareRename(server, position, filePath).flatMap { pr =>
              assert(pr.isDefined) // check if the rename is actually valid
              doRename(newName, server, goldenPath, content, position, filePath)
            }
          )
      })
  }

  def runTestDisabled(path: String): Future[Assertion] = withServer[Assertion](buildServer()) { server =>
    val original                = filePath(path)
    var content: Option[String] = None

    platform
      .fetchContent(original, AMFGraphConfiguration.predefined())
      .flatMap(contents => {

        val fileContentsStr = contents.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr, "*")
        content = Option(markerInfo.content)
        val position = markerInfo.position

        val filePath = s"file:///$path"
        openFile(server)(filePath, markerInfo.content)
          .flatMap(_ =>
            prepareRename(server, position, filePath).flatMap { pr =>
              assert(pr.isEmpty) // check if the rename is actually valid
            }
          )
      })
  }

  private def prepareRename(server: LanguageServer, position: Position, filePath: String) = {
    val prepareRenameHandler = server.resolveHandler(PrepareRenameRequestType).value
    prepareRenameHandler(
      PrepareRenameParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(position))
    )
  }

  private def doRename(
      newName: String,
      server: LanguageServer,
      goldenPath: String,
      content: Option[String],
      position: Position,
      filePath: String
  ) = {
    val renameHandler = server.resolveHandler(RenameRequestType).value
    renameHandler(RenameParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(position), newName))
      .flatMap(workspaceEdit => {
        closeFile(server)(filePath)
          .flatMap(_ => assertWorkspaceEdits(workspaceEdit, goldenPath, content))
      })
  }

}
