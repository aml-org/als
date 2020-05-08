package org.mulesoft.als.server.modules.reference

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.feature.common.{Location, LocationLink, TextDocumentIdentifier, TextDocumentPositionParams}
import org.mulesoft.lsp.feature.implementation.ImplementationRequestType
import org.mulesoft.lsp.feature.reference.{ReferenceContext, ReferenceParams, ReferenceRequestType}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait ServerReferencesTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "actions/reference"

  override def buildServer(): LanguageServer = {

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, factory.resolutionTaskManager)
      .addRequestModule(factory.referenceManager)
      .addRequestModule(factory.implementationManager)
      .build()
  }

  def runTest(path: String, expectedDefinitions: Set[Location]): Future[Assertion] =
    withServer[Assertion] { server =>
      val resolved = filePath(platform.encodeURI(path))
      for {
        content <- this.platform.resolve(resolved)
        definitions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr)

          getServerReferences(resolved, server, markerInfo)
        }
      } yield {
        assert(definitions.toSet == expectedDefinitions)
      }
    }

  def runTestImplementations(path: String, expectedDefinitions: Set[Location]): Future[Assertion] =
    withServer[Assertion] { server =>
      val resolved = filePath(platform.encodeURI(path))
      for {
        content <- this.platform.resolve(resolved)
        definitions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr)

          getServerImplementations(resolved, server, markerInfo)
        }
      } yield {
        assert(definitions.toSet == expectedDefinitions)
      }
    }

  def getServerReferences(filePath: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Seq[Location]] = {

    openFile(server)(filePath, markerInfo.patchedContent.original)

    val referenceHandler = server.resolveHandler(ReferenceRequestType).value

    referenceHandler(
      ReferenceParams(TextDocumentIdentifier(filePath),
                      LspRangeConverter.toLspPosition(markerInfo.position),
                      ReferenceContext(false)))
      .map(references => {
        closeFile(server)(filePath)
        references
      })
  }

  def getServerImplementations(filePath: String,
                               server: LanguageServer,
                               markerInfo: MarkerInfo): Future[Seq[Location]] = {

    openFile(server)(filePath, markerInfo.patchedContent.original)

    val implementationsHandler = server.resolveHandler(ImplementationRequestType).value

    implementationsHandler(
      TextDocumentPositionParams(TextDocumentIdentifier(filePath),
                                 LspRangeConverter.toLspPosition(markerInfo.position)))
      .map(implementations => {
        closeFile(server)(filePath)
        implementations
      })
      .map(_.left.getOrElse(Nil))
  }

  def findMarker(str: String, label: String = "[*]", cut: Boolean = true): MarkerInfo = {
    val offset = str.indexOf(label)

    if (offset < 0)
      new MarkerInfo(PatchedContent(str, str, Nil), Position(str.length, str))
    else {
      val rawContent = str.substring(0, offset) + str.substring(offset + label.length)
      val preparedContent =
        ContentPatcher(rawContent, offset, YAML).prepareContent()
      new MarkerInfo(preparedContent, Position(offset, str))
    }
  }
}

class MarkerInfo(val patchedContent: PatchedContent, val position: Position) {}
