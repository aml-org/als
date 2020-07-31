package org.mulesoft.als.server.modules.renameFile

import org.mulesoft.als.server.feature.renameFile.{
  RenameFileActionParams,
  RenameFileActionRequestType,
  RenameFileActionResult
}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.edit.RenameFile
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{Position, Range, TextDocumentIdentifier}

import scala.concurrent.{ExecutionContext, Future}

class RenameFileTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "actions/renameFile"

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.renameFileActionManager)
      .build()
  }

  test("Should work on plain document link") {
    val workspacePath = filePath(platform.encodeURI("plain"))
    val oldPath       = filePath(platform.encodeURI("plain/resourceType.raml"))
    val newPath       = filePath(platform.encodeURI("plain/RENAMED.raml"))

    runTest(buildServer(), workspacePath, oldPath, newPath).map(r => {
      r.edits.documentChanges.exists {
        case Right(value: RenameFile) => value.oldUri == oldPath && value.newUri == newPath
        case _                        => false
      } should be(true)

      r.edits.documentChanges.exists {
        case Left(edit) =>
          edit.textDocument.uri == filePath(platform.encodeURI("plain/api.raml")) &&
            edit.edits.size == 1 &&
            edit.edits.exists(e =>
              e.newText == "/RENAMED.raml" &&
                e.range == Range(Position(4, 25), Position(4, 42)))
        case _ => false
      } should be(true)

    })
  }

  test("Should work on encoded document link") {
    val workspacePath = filePath(platform.encodeURI("encoded"))
    val oldPath       = filePath(platform.encodeURI("encoded/resource type.raml"))
    val newPath       = filePath(platform.encodeURI("encoded/RENAMED.raml"))

    runTest(buildServer(), workspacePath, oldPath, newPath).map(r => {
      r.edits.documentChanges.exists {
        case Right(value: RenameFile) => value.oldUri == oldPath && value.newUri == newPath
        case _                        => false
      } should be(true)

      r.edits.documentChanges.exists {
        case Left(edit) =>
          edit.textDocument.uri == filePath(platform.encodeURI("encoded/api.raml")) &&
            edit.edits.size == 1 &&
            edit.edits.exists(e =>
              e.newText == "/RENAMED.raml" &&
                e.range == Range(Position(4, 25), Position(4, 43)))
        case _ => false
      } should be(true)

    })
  }

  test("Should work on pointer document link") {
    val workspacePath = filePath(platform.encodeURI("pointer"))
    val oldPath       = filePath(platform.encodeURI("pointer/get-endpoint.json"))
    val newPath       = filePath(platform.encodeURI("pointer/RENAMED.json"))

    runTest(buildServer(), workspacePath, oldPath, newPath).map(r => {
      r.edits.documentChanges.exists {
        case Right(value: RenameFile) => value.oldUri == oldPath && value.newUri == newPath
        case _                        => false
      } should be(true)

      r.edits.documentChanges.exists {
        case Left(edit) =>
          edit.textDocument.uri == filePath(platform.encodeURI("pointer/api.json")) &&
            edit.edits.size == 1 &&
            edit.edits.exists(e =>
              e.newText == "/RENAMED.json" &&
                e.range == Range(Position(8, 15), Position(8, 32)))
        case _ => false
      } should be(true)
    })
  }

  test("Should edit multiple files") {
    val workspacePath = filePath(platform.encodeURI("relative"))
    val oldPath       = filePath(platform.encodeURI("relative/securedTrait.raml"))
    val newPath       = filePath(platform.encodeURI("relative/RENAMED.raml"))

    runTest(buildServer(), workspacePath, oldPath, newPath).map(r => {
      r.edits.documentChanges.exists {
        case Right(value: RenameFile) => value.oldUri == oldPath && value.newUri == newPath
        case _                        => false
      } should be(true)

      r.edits.documentChanges.exists {
        case Left(edit) =>
          edit.textDocument.uri == filePath(platform.encodeURI("relative/traits/trait.raml")) &&
            edit.edits.size == 1 &&
            edit.edits.exists(e =>
              e.newText == "/RENAMED.raml" &&
                e.range == Range(Position(1, 13), Position(1, 33)))
        case Right(_) => false
      } should be(true)

      r.edits.documentChanges.exists {
        case Left(edit) =>
          edit.textDocument.uri == filePath(platform.encodeURI("relative/traits/trait2.raml")) &&
            edit.edits.size == 1 &&
            edit.edits.exists(e =>
              e.newText == "/RENAMED.raml" &&
                e.range == Range(Position(1, 13), Position(1, 33)))
        case Right(_) => false
      } should be(true)
    })
  }

  def runTest(server: LanguageServer,
              workspaceUri: String,
              oldFilePath: String,
              newFilePath: String): Future[RenameFileActionResult] = {

    val oldFile          = TextDocumentIdentifier(oldFilePath)
    val newFile          = TextDocumentIdentifier(newFilePath)
    val workspaceService = server.workspaceService.asInstanceOf[WorkspaceManager]
    for {
      _ <- workspaceService.initializeWS(workspaceUri)
      renameFileResult <- {
        val handler: RequestHandler[RenameFileActionParams, RenameFileActionResult] =
          server.resolveHandler(RenameFileActionRequestType).get
        handler(RenameFileActionParams(oldFile, newFile))
      }
    } yield {
      renameFileResult
    }
  }

}
