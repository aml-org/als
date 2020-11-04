package org.mulesoft.als.server.modules.definition

import org.mulesoft.als.common.{MarkerFinderTest, MarkerInfo}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.feature.common.{LocationLink, TextDocumentIdentifier}
import org.mulesoft.lsp.feature.definition.{DefinitionParams, DefinitionRequestType}
import org.mulesoft.lsp.feature.typedefinition.{TypeDefinitionParams, TypeDefinitionRequestType}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

trait ServerDefinitionTest extends LanguageServerBaseTest with MarkerFinderTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def rootPath: String = "actions/definition"

  def buildServer(): LanguageServer = {

    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager,
                              factory.workspaceManager,
                              factory.configurationManager,
                              factory.resolutionTaskManager)
      .addRequestModule(factory.definitionManager)
      .addRequestModule(factory.typeDefinitionManager)
      .build()
  }

  def runTest(path: String, expectedDefinitions: Set[LocationLink]): Future[Assertion] =
    withServer[Assertion](buildServer()) { server =>
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

  def runTestTypeDefinition(path: String, expectedDefinitions: Set[LocationLink]): Future[Assertion] =
    withServer[Assertion](buildServer()) { server =>
      val resolved = filePath(platform.encodeURI(path))
      for {
        content <- this.platform.resolve(resolved)
        definitions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr)

          getServerTypeDefinition(resolved, server, markerInfo)
        }
      } yield {
        assert(definitions.toSet == expectedDefinitions)
      }
    }

  def getServerDefinition(filePath: String,
                          server: LanguageServer,
                          markerInfo: MarkerInfo): Future[Seq[LocationLink]] = {

    openFile(server)(filePath, markerInfo.content)

    val definitionHandler = server.resolveHandler(DefinitionRequestType).value

    definitionHandler(
      DefinitionParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(markerInfo.position)))
      .map(definitions => {
        closeFile(server)(filePath)

        definitions.right.getOrElse(Nil)
      })
  }

  def getServerTypeDefinition(filePath: String,
                              server: LanguageServer,
                              markerInfo: MarkerInfo): Future[Seq[LocationLink]] = {

    openFile(server)(filePath, markerInfo.content)

    val definitionHandler = server.resolveHandler(TypeDefinitionRequestType).value

    definitionHandler(
      TypeDefinitionParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(markerInfo.position)))
      .map(definitions => {
        closeFile(server)(filePath)

        definitions.right.getOrElse(Nil)
      })
  }

}
