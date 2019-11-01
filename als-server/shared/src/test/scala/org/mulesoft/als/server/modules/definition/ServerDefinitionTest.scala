package org.mulesoft.als.server.modules.definition

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.als.suggestions.Core
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.common.{LocationLink, TextDocumentIdentifier, TextDocumentPositionParams}
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.definition.DefinitionRequestType
import org.mulesoft.lsp.server.LanguageServer
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait ServerDefinitionTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "actions/definition"

  override def buildServer(): LanguageServer = {

    val factory = ManagersFactory(MockDiagnosticClientNotifier, platform, logger)
    new LanguageServerBuilder(factory.documentManager)
      .addInitializable(factory.astManager)
      .addRequestModule(factory.definitionManager)
      .build()
  }

  def runTest(path: String, expectedDefinitions: Set[LocationLink]): Future[Assertion] = withServer[Assertion] {
    server =>
      val resolved = filePath(platform.encodeURI(path))
      for {
        content <- this.platform.resolve(resolved)
        definitions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr)

          getServerDefinition(resolved, server, markerInfo)
        }
      } yield {
        assert(definitions.toSet == expectedDefinitions)
      }
  }

  def getServerDefinition(filePath: String,
                          server: LanguageServer,
                          markerInfo: MarkerInfo): Future[Seq[LocationLink]] = {

    openFile(server)(filePath, markerInfo.patchedContent.original)

    val definitionHandler = server.resolveHandler(DefinitionRequestType).value

    definitionHandler(
      TextDocumentPositionParams(TextDocumentIdentifier(filePath),
                                 LspRangeConverter.toLspPosition(markerInfo.position)))
      .map(definitions => {
        closeFile(server)(filePath)

        definitions.right.getOrElse(Nil)
      })
  }

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
