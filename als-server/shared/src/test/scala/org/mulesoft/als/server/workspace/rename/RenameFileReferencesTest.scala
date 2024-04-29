package org.mulesoft.als.server.workspace.rename

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.EditorConfiguration
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.edit.{RenameFile, TextDocumentEdit, TextEdit, WorkspaceEdit}
import org.mulesoft.lsp.feature.common.{Position, Range, TextDocumentIdentifier, VersionedTextDocumentIdentifier}
import org.mulesoft.lsp.feature.rename.{RenameParams, RenameRequestType}
import org.scalatest.{Assertion, Succeeded}

import scala.concurrent.{ExecutionContext, Future}

class RenameFileReferencesTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global
  override def rootPath: String = ""

  private val ws1 = Map(
    "file:///root/exchange.json" -> """{"main": "api.raml"}""",
    "file:///root/api.raml" ->
      """#%RAML 1.0
        |title: !include ref.txt
        |uses:
        |  lib: lib.raml""".stripMargin,
    "file:///root/ref.txt" -> "test reference",
    "file:///root/lib.raml" ->
      """#%RAML 1.0 Library
        |types:
        |  A:
        |    type: string
        |    example: !include ref.txt""".stripMargin
  )

  val testSets: Set[TestEntry] = Set(
    TestEntry(
      "file:///root/api.raml",
      Position(1, 19),
      "reference.txt",
      ws1,
      WorkspaceEdit(
        None,
        Some(
          List(
            Right(RenameFile("file:///root/ref.txt", "file:///root/reference.txt", None)),
            Left(
              TextDocumentEdit(
                VersionedTextDocumentIdentifier("file:///root/api.raml", None),
                List(TextEdit(Range(Position(1, 16), Position(1, 23)), "reference.txt"))
              )
            ),
            Left(
              TextDocumentEdit(
                VersionedTextDocumentIdentifier("file:///root/lib.raml", None),
                List(TextEdit(Range(Position(4, 22), Position(4, 29)), "reference.txt"))
              )
            )
          )
        )
      )
    )
  )

  ignore("Rename file through a reference - through handler") { // when enabling rename through references
    Future
      .sequence {
        testSets.map { test =>
          runTest(test.root, test.ws, test.targetUri, test.targetPosition, test.newName, test.result)
        }
      }
      .map(r => assert(r.forall(_ == Succeeded)))
  }

  def runTest(
      root: String,
      ws: Map[String, String],
      searchedUri: String,
      position: Position,
      newName: String,
      expectedResult: WorkspaceEdit
  ): Future[Assertion] =
    for {
      (server, _) <- buildServer(root, ws)
      result      <- getServerRename(server, searchedUri, position, newName)
    } yield {
      val assertion = equalWSE(result, expectedResult)
      if (!assertion)
        println(s"result: $result\nexpected: $expectedResult")
      assertion should be(true)
    }

  def getServerRename(server: LanguageServer, uri: String, position: Position, newName: String): Future[WorkspaceEdit] =
    server
      .resolveHandler(RenameRequestType)
      .map { _(RenameParams(TextDocumentIdentifier(uri), position, newName)) }
      .getOrElse(Future.failed(new Exception("No handler found for FileUsage")))

  private def equalWSE(a: WorkspaceEdit, b: WorkspaceEdit): Boolean =
    a.changes.getOrElse(Map.empty).mapValues(_.toSet) == b.changes.getOrElse(Map.empty).mapValues(_.toSet) &&
      a.documentChanges.getOrElse(Seq.empty).map(_.left) == b.documentChanges.getOrElse(Seq.empty).map(_.left)

  case class TestEntry(
      targetUri: String,
      targetPosition: Position,
      newName: String,
      ws: Map[String, String],
      result: WorkspaceEdit,
      root: String = "file:///root"
  )

  def buildServer(root: String, ws: Map[String, String]): Future[(LanguageServer, WorkspaceManager)] = {
    val rl = new ResourceLoader {
      override def fetch(resource: String): Future[Content] =
        ws.get(resource)
          .map(c => new Content(c, resource))
          .map(Future.successful)
          .getOrElse(Future.failed(new Exception("File not found on custom ResourceLoader")))
      override def accepts(resource: String): Boolean =
        ws.keySet.contains(resource)
    }

    val factory =
      new WorkspaceManagerFactoryBuilder(
        new MockDiagnosticClientNotifier,
        EditorConfiguration.withPlatformLoaders(Seq(rl))
      )
        .buildWorkspaceManagerFactory()

    val workspaceManager: WorkspaceManager = factory.workspaceManager
    val server =
      new LanguageServerBuilder(
        factory.documentManager,
        workspaceManager,
        factory.configurationManager,
        factory.resolutionTaskManager
      )
        .addRequestModule(factory.renameManager)
        .build()

    server
      .testInitialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
      .andThen { case _ => server.initialized() }
      .map(_ => (server, workspaceManager))

  }
}
