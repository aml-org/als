package org.mulesoft.als.server.modules.definition

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.telemetry.TelemetryManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.{TextDocumentIdentifier, TextDocumentPositionParams, Position => lspPosition}
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.definition.DefinitionRequestType

import scala.concurrent.ExecutionContext

class ServerDefinitionTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  override def rootPath: String                            = ""

  override def addModules(documentManager: TextDocumentManager,
                          platform: Platform,
                          directoryResolver: DirectoryResolver,
                          baseEnvironment: Environment,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val telemetryManager = new TelemetryManager(MockClientNotifier, logger)

    val astManager = new AstManager(documentManager, baseEnvironment, telemetryManager, platform, logger)

    builder
      .addInitializable(astManager)
  }

  ignore("Open declaration test 001") {
    withServer { server =>
      var content1 =
        """#%RAML 1.0
          |title: test
          |types:
          |  MyType:
          |  MyType2:
          |    properties:
          |      p1: MyType
          |""".stripMargin
      val ind           = content1.indexOf("p1: MyType") + "p1: My".length
      val usagePosition = LspRangeConverter.toLspPosition(Position(ind, content1))

      val url = "file:///findDeclarationTest001.raml"

      openFile(server)(url, content1)

      val handler = server.resolveHandler(DefinitionRequestType).value

      handler(new TextDocumentPositionParams() {
        override val textDocument: TextDocumentIdentifier = TextDocumentIdentifier(url)
        override val position: lspPosition                = usagePosition
      }).map(declarations => {
        closeFile(server)(url)

        if (declarations.isLeft) {
          succeed
        } else {
          fail("No references have been found")
        }
      })

    }
  }
}
