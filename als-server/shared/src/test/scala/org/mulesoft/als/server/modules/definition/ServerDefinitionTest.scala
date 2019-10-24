package org.mulesoft.als.server.modules.definition

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.actions.GoToDefinitionManager
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.lsp.common.{LocationLink, TextDocumentIdentifier, TextDocumentPositionParams}
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.definition.DefinitionRequestType
import org.mulesoft.lsp.server.LanguageServer
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait ServerDefinitionTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "actions/definition"

  override def addModules(documentManager: TextDocumentManager,
                          platform: Platform,
                          directoryResolver: DirectoryResolver,
                          baseEnvironment: Environment,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val telemetryManager = new TelemetryManager(MockDiagnosticClientNotifier, logger)

    val astManager = new AstManager(documentManager, baseEnvironment, telemetryManager, platform, logger)

    val definitionsManager = new GoToDefinitionManager(astManager, telemetryManager, logger, platform)

    builder
      .addInitializable(astManager)
      .addRequestModule(definitionsManager)
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

    openFile(server)(filePath, markerInfo.rawContent)

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
      new MarkerInfo(str, Position(str.length, str), str)
    else {
      val rawContent = str.substring(0, offset) + str.substring(offset + label.length)
      val preparedContent =
        org.mulesoft.als.suggestions.Core.prepareText(rawContent, offset, YAML)
      new MarkerInfo(preparedContent, Position(offset, str), rawContent)
    }
  }
}

class MarkerInfo(val content: String, val position: Position, val rawContent: String) {}
