package org.mulesoft.als.server.modules.rename

import org.mulesoft.als.common.MarkerFinderTest
import org.mulesoft.als.common.diff.WorkspaceEditsTest
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.rename.{PrepareRenameParams, PrepareRenameRequestType, RenameParams, RenameRequestType}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

abstract class ServerRenameTest extends LanguageServerBaseTest with WorkspaceEditsTest with MarkerFinderTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addInitializable(factory.documentManager)
      .addRequestModule(factory.renameManager)
      .build()
  }

  def runTest(path: String, newName: String): Future[Assertion] = withServer[Assertion](buildServer()) { server =>
    val resultPath                     = path.replace(".", "-renamed.")
    val resolved                       = filePath(path)
    val resolvedResultPath             = filePath(resultPath)
    var renamedContent: Option[String] = None
    var content: Option[String]        = None

    Future
      .sequence(List(platform.resolve(resolved), platform.resolve(resolvedResultPath)))
      .flatMap(contents => {

        val fileContentsStr        = contents.head.stream.toString
        val renamedFileContentsStr = contents.last.stream.toString
        renamedContent = Option(renamedFileContentsStr.trim)
        val markerInfo = this.findMarker(fileContentsStr, "*")
        content = Option(markerInfo.content)
        val position = markerInfo.position

        val filePath = s"file:///$path"
        openFile(server)(filePath, markerInfo.content)
        val renameHandler        = server.resolveHandler(RenameRequestType).value
        val prepareRenameHandler = server.resolveHandler(PrepareRenameRequestType).value
        prepareRenameHandler(
          PrepareRenameParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(position)))
          .flatMap { pr =>
            assert(pr.isDefined) // check if the rename is actually valid
            renameHandler(
              RenameParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(position), newName))
              .map(workspaceEdit => {
                closeFile(server)(filePath)
                assertWorkspaceEdits(workspaceEdit, renamedContent, content, path)
              })
          }
      })
  }

}
