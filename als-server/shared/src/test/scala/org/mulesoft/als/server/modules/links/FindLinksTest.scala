package org.mulesoft.als.server.modules.links

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.link.{DocumentLink, DocumentLinkParams, DocumentLinkRequestType}
import org.mulesoft.lsp.server.LanguageServer
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait FindLinksTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "actions/links"

  override def buildServer(): LanguageServer = {

    val managers =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(managers.documentManager, managers.workspaceManager)
      .addRequestModule(managers.documentLinksManager)
      .build()
  }

  def runTest(path: String, expectedDefinitions: Set[DocumentLink]): Future[Assertion] = withServer[Assertion] {
    server =>
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

    openFile(server)(filePath, markerInfo.patchedContent.original)

    val linksHandler = server.resolveHandler(DocumentLinkRequestType).value

    linksHandler(DocumentLinkParams(TextDocumentIdentifier(filePath)))
      .map(links => {
        closeFile(server)(filePath)
        links
      })
  }

  // TODO: extract MarkerInfo related to trait?
  def findMarker(str: String, label: String = "[*]", cut: Boolean = true): MarkerInfo = {
    val offset = str.indexOf(label)

    if (offset < 0)
      new MarkerInfo(PatchedContent(str, str, Nil), Position(str.length, str))
    else {
      val rawContent      = str.substring(0, offset) + str.substring(offset + label.length)
      val preparedContent = ContentPatcher(rawContent, offset, YAML).prepareContent()
      new MarkerInfo(preparedContent, Position(offset, str))
    }
  }
}

class MarkerInfo(val patchedContent: PatchedContent, val position: Position) {}
