package org.mulesoft.als.server.modules.links

import org.mulesoft.als.common.{MarkerFinderTest, MarkerInfo}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.link.{DocumentLink, DocumentLinkParams, DocumentLinkRequestType}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait FindLinksTest extends LanguageServerBaseTest with MarkerFinderTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "actions/links"

  def buildServer(): LanguageServer = {

    val managers =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(managers.documentManager,
                              managers.workspaceManager,
                              managers.configurationManager,
                              managers.resolutionTaskManager)
      .addRequestModule(managers.documentLinksManager)
      .build()
  }

  def runTest(path: String, expectedDefinitions: Set[DocumentLink]): Future[Assertion] =
    withServer[Assertion](buildServer()) { server =>
      val resolved = filePath(platform.encodeURI(path))
      for {
        content <- this.platform.resolve(resolved)
        definitions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr)

          getServerLinks(resolved, server, markerInfo)
        }
      } yield {
        assert(definitions.toSet == expectedDefinitions)
      }
    }

  def getServerLinks(filePath: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Seq[DocumentLink]] = {

    openFile(server)(filePath, markerInfo.content)

    val linksHandler = server.resolveHandler(DocumentLinkRequestType).value

    linksHandler(DocumentLinkParams(TextDocumentIdentifier(filePath)))
      .map(links => {
        closeFile(server)(filePath)
        links
      })
  }

}
